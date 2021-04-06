package app.mirru;
import android.provider.MediaStore;
import android.util.Log;

import com.google.mediapipe.framework.Packet;

import mikera.vectorz.Vector3;

public class FingerAngles {
    public static Vector3 fingerDir(Vector3 startingPoint, Vector3 terminalPoint) {
        terminalPoint.sub(startingPoint);
        Vector3 direction = new Vector3(terminalPoint);
        direction.toNormal();
        return direction;
    }

    public static Vector3 getNormal(Vector3 A, Vector3 B, Vector3 C) {
        B.sub(A);
        Vector3 side1 = B;

        C.sub(A);
        Vector3 side2 = C;

        side1.crossProduct(side2);

        Vector3 normal = new Vector3(side1.toNormal());
        return normal;
    }

    public static Vector3 getThumbNormal(Vector3 A, Vector3 B, Vector3 C, Vector3 D) {
        B.sub(A);
        Vector3 side1 = B;

        D.sub(C);
        Vector3 side2 = D;

        side1.crossProduct(side2);

        Vector3 normal = new Vector3(side1.toNormal());
        return normal;
    }

    public static double servoAngle(Vector3 fingerDir, Vector3 normal, boolean isThumb) {
        //angle calculation by:
        //https://www.instructables.com/Robotic-Hand-controlled-by-Gesture-with-Arduino-Le/
        double scalarProduct = normal.x * fingerDir.x + normal.y * fingerDir.y + normal.z * fingerDir.z;
        double palm_module = Math.sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z);
        double finger_module = Math.sqrt(fingerDir.x * fingerDir.x + fingerDir.y * fingerDir.y + fingerDir.z * fingerDir.z);
        double angle_radians = Math.acos(scalarProduct / (palm_module * finger_module));
        double angle_degrees = angle_radians * 180 / Math.PI;

        double servoAngle = 0;

        switch (MediapipeFragment.isHandLeft) {
            case "Left":
                if (!isThumb) {
                    servoAngle = (160 - (100 - angle_degrees) * 1.8); // EMPIRICAL CONVERSION, MAY BE DIFFERENT FOR DIFFERENT SERVOS!
                } else {
                    servoAngle = (80-(100-angle_degrees)*1.5);; // EMPIRICAL CONVERSION, MAY BE DIFFERENT FOR DIFFERENT SERVOS
                }
                break;
            case "Right":
                if (!isThumb) {
                    servoAngle = (130 + (100 - angle_degrees) * 1.5); // EMPIRICAL CONVERSION, MAY BE DIFFERENT FOR DIFFERENT SERVOS!
                } else {
                    servoAngle = (30+(10+angle_degrees) * 1.2); // EMPIRICAL CONVERSION, MAY BE DIFFERENT FOR DIFFERENT SERVOS
                }
                break;
        }
        return servoAngle;
    }

//    static float getAvg(float prev_avg, float x, int n)
//    {
//        return (prev_avg * n + x) / (n + 1);
//    }
}