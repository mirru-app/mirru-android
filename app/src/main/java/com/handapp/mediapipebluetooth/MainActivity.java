package com.handapp.mediapipebluetooth;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList;
import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.components.CameraXPreviewHelper;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.framework.AndroidPacketCreator;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.glutil.EglManager;
import com.google.protobuf.InvalidProtocolBufferException;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mikera.vectorz.Vector2;
import mikera.vectorz.Vector3;

/**
 * Main activity of MediaPipe example apps.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String BINARY_GRAPH_NAME = "hand_tracking_mobile_gpu.binarypb";
    private static final String INPUT_VIDEO_STREAM_NAME = "input_video";
    private static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "hand_landmarks";
    private static final String INPUT_NUM_HANDS_SIDE_PACKET_NAME = "num_hands";
    private static final int NUM_HANDS = 1;
    private static final CameraHelper.CameraFacing CAMERA_FACING = CameraHelper.CameraFacing.FRONT;
    // Flips the camera-preview frames vertically before sending them into FrameProcessor to be
    // processed in a MediaPipe graph, and flips the processed frames back when they are displayed.
    // This is needed because OpenGL represents images assuming the image origin is at the bottom-left
    // corner, whereas MediaPipe in general assumes the image origin is at top-left.
    private static final boolean FLIP_FRAMES_VERTICALLY = true;

    static {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni");
        System.loadLibrary("opencv_java3");
    }

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private SurfaceTexture previewFrameTexture;
    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    private SurfaceView previewDisplayView;
    // Creates and manages an {@link EGLContext}.
    private EglManager eglManager;
    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    private FrameProcessor processor;
    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private ExternalTextureConverter converter;
    // ApplicationInfo for retrieving metadata defined in the manifest.
    private ApplicationInfo applicationInfo;
    // Handles camera access via the {@link CameraX} Jetpack support library.
    private CameraXPreviewHelper cameraHelper;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutResId());

        try {
            applicationInfo =
                    getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Cannot find application info: " + e);
        }

        previewDisplayView = new SurfaceView(this);
        setupPreviewDisplayView();

        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
        // binary graphs.
        AndroidAssetUtil.initializeNativeAssetManager(this);
        eglManager = new EglManager(null);
        processor =
                new FrameProcessor(
                        this,
                        eglManager.getNativeContext(),
                        BINARY_GRAPH_NAME,
                        INPUT_VIDEO_STREAM_NAME,
                        OUTPUT_VIDEO_STREAM_NAME);
        processor
                .getVideoSurfaceOutput()
                .setFlipY(FLIP_FRAMES_VERTICALLY);

        PermissionHelper.checkAndRequestCameraPermissions(this);
        AndroidPacketCreator packetCreator = processor.getPacketCreator();
        Map<String, Packet> inputSidePackets = new HashMap<>();
        inputSidePackets.put(INPUT_NUM_HANDS_SIDE_PACKET_NAME, packetCreator.createInt32(NUM_HANDS));
        processor.setInputSidePackets(inputSidePackets);

//        // To show verbose logging, run:
        // adb shell setprop log.tag.MainActivity VERBOSE
        processor.addPacketCallback(
                OUTPUT_LANDMARKS_STREAM_NAME,
                (packet) -> {
                    List<NormalizedLandmarkList> multiHandLandmarks =
                            PacketGetter.getProtoVector(packet, NormalizedLandmarkList.parser());
                    Log.i(
                            TAG, getCirclesOfFingersString(multiHandLandmarks));
                });
    }

    // Used to obtain the content view for this application. If you are extending this class, and
    // have a custom layout, override this method and return the custom layout.
    protected int getContentViewLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        converter =
                new ExternalTextureConverter(
                        eglManager.getContext(), 2);
        converter.setFlipY(FLIP_FRAMES_VERTICALLY);
        converter.setConsumer(processor);
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        converter.close();

        // Hide preview display until we re-open the camera again.
        previewDisplayView.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onCameraStarted(SurfaceTexture surfaceTexture) {
        previewFrameTexture = surfaceTexture;
        // Make the display view visible to start showing the preview. This triggers the
        // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
        previewDisplayView.setVisibility(View.VISIBLE);
    }

    protected Size cameraTargetResolution() {
        return null; // No preference and let the camera (helper) decide.
    }

    public void startCamera() {
        cameraHelper = new CameraXPreviewHelper();
        cameraHelper.setOnCameraStartedListener(
                surfaceTexture -> {
                    onCameraStarted(surfaceTexture);
                });
        CameraHelper.CameraFacing cameraFacing = CameraHelper.CameraFacing.FRONT;
        cameraHelper.startCamera(
                this, cameraFacing, /*unusedSurfaceTexture=*/ null, cameraTargetResolution());
    }

    protected Size computeViewSize(int width, int height) {
        return new Size(width, height);
    }

    protected void onPreviewDisplaySurfaceChanged(
            SurfaceHolder holder, int format, int width, int height) {
        // (Re-)Compute the ideal size of the camera-preview display (the area that the
        // camera-preview frames get rendered onto, potentially with scaling and rotation)
        // based on the size of the SurfaceView that contains the display.
        Size viewSize = computeViewSize(width, height);
        Size displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize);
        boolean isCameraRotated = cameraHelper.isCameraRotated();

        // Connect the converter to the camera-preview frames as its input (via
        // previewFrameTexture), and configure the output width and height as the computed
        // display size.
        converter.setSurfaceTextureAndAttachToGLContext(
                previewFrameTexture,
                isCameraRotated ? displaySize.getHeight() : displaySize.getWidth(),
                isCameraRotated ? displaySize.getWidth() : displaySize.getHeight());
    }

    private void setupPreviewDisplayView() {
        previewDisplayView.setVisibility(View.GONE);
        ViewGroup viewGroup = findViewById(R.id.preview_display_layout);
        viewGroup.addView(previewDisplayView);

        previewDisplayView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(holder.getSurface());
                            }

                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                onPreviewDisplaySurfaceChanged(holder, format, width, height);
                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(null);
                            }
                        });
    }

    private String getMultiHandLandmarksDebugString(List<NormalizedLandmarkList> multiHandLandmarks) {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand landmarks";
        }
        String multiHandLandmarksStr = "";
        int handIndex = 0;
        for (NormalizedLandmarkList landmarks : multiHandLandmarks) {
//            multiHandLandmarksStr +=
//                    "\t#Hand landmarks for hand[" + handIndex + "]: " + landmarks.getLandmarkCount() + "\n";
            int landmarkIndex = 0;
            for (NormalizedLandmark landmark : landmarks.getLandmarkList()) {

//                if (landmarkIndex == 8) {
                multiHandLandmarksStr +=
                        "\t\tLandmark ["
                                + landmarkIndex
                                + "]: ("
                                + landmark.getX()
                                + ", "
                                + landmark.getY()
                                + ", "
                                + landmark.getZ()
                                + ")\n";
//                }
                ++landmarkIndex;
            }
            ++handIndex;
        }
        return multiHandLandmarksStr;
    }

    int counter = 0;

    String fingerCirclesString = "0,0,0,0";
    Vector3 thumb1;
    Vector3 thumb2;
    Vector3 thumb3;

    Vector3 index1;
    Vector3 index2;
    Vector3 index3;

    Vector3 mid1;
    Vector3 mid2;
    Vector3 mid3;

    Vector3 ring1;
    Vector3 ring2;
    Vector3 ring3;

    List<Vector3> thumbList1 = new ArrayList<>();
    List<Vector3> thumbList2 = new ArrayList<>();
    List<Vector3> thumbList3 = new ArrayList<>();

    List<Vector3> indexList1 = new ArrayList<>();
    List<Vector3> indexList2 = new ArrayList<>();
    List<Vector3> indexList3 = new ArrayList<>();

    List<Vector3> midList1 = new ArrayList<>();
    List<Vector3> midList2 = new ArrayList<>();
    List<Vector3> midList3 = new ArrayList<>();

    List<Vector3> ringList1 = new ArrayList<>();
    List<Vector3> ringList2 = new ArrayList<>();
    List<Vector3> ringList3 = new ArrayList<>();

    private String getCirclesOfFingersString(List<NormalizedLandmarkList> multiHandLandmarks) {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand landmarks";
        }

        for (NormalizedLandmarkList landmarks : multiHandLandmarks) {
            int max = 10;
            System.out.println(counter);

            thumb1 = Vector3.of(landmarks.getLandmark(2).getX(), landmarks.getLandmark(2).getY(), landmarks.getLandmark(2).getZ());
            thumb2 = Vector3.of(landmarks.getLandmark(3).getX(), landmarks.getLandmark(3).getY(), landmarks.getLandmark(3).getZ());
            thumb3 = Vector3.of(landmarks.getLandmark(4).getX(), landmarks.getLandmark(4).getY(), landmarks.getLandmark(4).getZ());
            thumbList1.add(thumb1);
            thumbList2.add(thumb2);
            thumbList3.add(thumb3);

            index1 = Vector3.of(landmarks.getLandmark(5).getX(), landmarks.getLandmark(5).getY(), landmarks.getLandmark(5).getZ());
            index2 = Vector3.of(landmarks.getLandmark(7).getX(), landmarks.getLandmark(7).getY(), landmarks.getLandmark(7).getZ());
            index3 = Vector3.of(landmarks.getLandmark(8).getX(), landmarks.getLandmark(8).getY(), landmarks.getLandmark(8).getZ());
            indexList1.add(index1);
            indexList2.add(index2);
            indexList3.add(index3);

            mid1 = Vector3.of(landmarks.getLandmark(10).getX(), landmarks.getLandmark(10).getY(), landmarks.getLandmark(10).getZ());
            mid2 = Vector3.of(landmarks.getLandmark(11).getX(), landmarks.getLandmark(11).getY(), landmarks.getLandmark(11).getZ());
            mid3 = Vector3.of(landmarks.getLandmark(12).getX(), landmarks.getLandmark(12).getY(), landmarks.getLandmark(12).getZ());

            midList1.add(mid1);
            midList2.add(mid2);
            midList3.add(mid3);

            ring1 = Vector3.of(landmarks.getLandmark(14).getX(), landmarks.getLandmark(14).getY(), landmarks.getLandmark(14).getZ());
            ring2 = Vector3.of(landmarks.getLandmark(15).getX(), landmarks.getLandmark(15).getY(), landmarks.getLandmark(15).getZ());
            ring3 = Vector3.of(landmarks.getLandmark(16).getX(), landmarks.getLandmark(16).getY(), landmarks.getLandmark(16).getZ());

            ringList1.add(ring1);
            ringList2.add(ring2);
            ringList3.add(ring3);

            if (ringList1.size() == max) {
                Vector3[] thumbArray1 = new Vector3[thumbList1.size()];
                thumbList1.toArray(thumbArray1);
                System.out.print("asdfsdf " + thumbArray1.length);
                Vector3[] thumbArray2 = thumbList2.toArray(new Vector3[0]);
                Vector3[] thumbArray3 = thumbList3.toArray(new Vector3[0]);

                Vector3[] indexArray1 = indexList1.toArray(new Vector3[0]);
                Vector3[] indexArray2 = indexList2.toArray(new Vector3[0]);
                Vector3[] indexArray3 = indexList3.toArray(new Vector3[0]);

                Vector3[] midArray1 = midList1.toArray(new Vector3[0]);
                Vector3[] midArray2 = midList2.toArray(new Vector3[0]);
                Vector3[] midArray3 = midList3.toArray(new Vector3[0]);

                Vector3[] ringArray1 = ringList1.toArray(new Vector3[0]);
                Vector3[] ringArray2 = ringList2.toArray(new Vector3[0]);
                Vector3[] ringArray3 = ringList3.toArray(new Vector3[0]);

                Vector3 thumbSmoothed1 = Average.streamAvg(thumbArray1, max);
                Vector3 thumbSmoothed2 = Average.streamAvg(thumbArray2, max);
                Vector3 thumbSmoothed3 = Average.streamAvg(thumbArray3, max);

                Vector3 indexSmoothed1 = Average.streamAvg(indexArray1, max);
                Vector3 indexSmoothed2 = Average.streamAvg(indexArray2, max);
                Vector3 indexSmoothed3 = Average.streamAvg(indexArray3, max);

                Vector3 midSmoothed1 = Average.streamAvg(midArray1, max);
                Vector3 midSmoothed2 = Average.streamAvg(midArray2, max);
                Vector3 midSmoothed3 = Average.streamAvg(midArray3, max);

                Vector3 ringSmoothed1 = Average.streamAvg(ringArray1, max);
                Vector3 ringSmoothed2 = Average.streamAvg(ringArray2, max);
                Vector3 ringSmoothed3 = Average.streamAvg(ringArray3, max);

                thumbList1.clear();
                thumbList2.clear();
                thumbList3.clear();

                indexList1.clear();
                indexList2.clear();
                indexList3.clear();

                midList1.clear();
                midList2.clear();
                midList3.clear();

                ringList1.clear();
                ringList2.clear();
                ringList3.clear();

                List rotatedT = FingerCircles.rotatePoints(thumbSmoothed1, thumbSmoothed2, thumbSmoothed3);
                Vector3[] rotatedPointsT = (Vector3[])rotatedT.get(1);
                float thumbAngle = FingerCircles.getAngle(rotatedPointsT[0], rotatedPointsT[1], rotatedPointsT[2], true);

                List rotatedI = FingerCircles.rotatePoints(indexSmoothed1, indexSmoothed2, indexSmoothed3);
                Vector3[] rotatedPointsI = (Vector3[])rotatedI.get(1);
                float indexAngle = FingerCircles.getAngle(rotatedPointsI[0], rotatedPointsI[1], rotatedPointsI[2], false);

                List rotatedM = FingerCircles.rotatePoints(midSmoothed1, midSmoothed2, midSmoothed3);
                Vector3[] rotatedPointsM = (Vector3[])rotatedM.get(1);
                float midAngle = FingerCircles.getAngle(rotatedPointsM[0], rotatedPointsM[1], rotatedPointsM[2], false);

                List rotatedR = FingerCircles.rotatePoints(ringSmoothed1, ringSmoothed2, ringSmoothed3);
                Vector3[] rotatedPointsR = (Vector3[])rotatedR.get(1);
                float ringAngle = FingerCircles.getAngle(rotatedPointsR[0], rotatedPointsR[1], rotatedPointsR[2], false);

                fingerCirclesString = (int)thumbAngle + "," + (int)indexAngle + "," + (int)midAngle + "," + (int)ringAngle;
                Log.i(TAG, fingerCirclesString);
                counter = 0;
            } else {
                counter ++;
            }
        }
        return fingerCirclesString;
    }
}