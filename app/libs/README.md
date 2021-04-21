# libs

This project currently uses a single Android lib (aar) which contains
the MediaPipe hand tracking calculator.

To re-build the library, you'll want to use the environment provided by the Docker container included with the mediapipe repository:

Additional instructions can be found [here](https://google.github.io/mediapipe/getting_started/android_archive_library.html#steps-to-build-a-mediapipe-aar).

```console
 $ docker build --tag=mediapipe ./mediapipe

 # Note: Mounting BUILD file
 $ docker run -it --name mediapipe \
    -v "$PWD/BUILD:/mediapipe/mediapipe/examples/android/src/java/com/google/mediapipe/apps/handtracking_aar/BUILD" \
    mediapipe:latest
```

Within the container terminal, run the following to build the aar:

```console
$ apt-get update

# They forgot to include this dependency in the Dockerfile
$ apt-get install zip

# You also need to install Android SDK + NDK. Kind of annoying...
$ bash ./setup_android_sdk_and_ndk.sh

$ bazel build -c opt --host_crosstool_top=@bazel_tools//tools/cpp:toolchain --fat_apk_cpu=arm64-v8a,armeabi-v7a //mediapipe/examples/android/src/java/com/google/mediapipe/apps/handtracking_aar:mp_handtracking_aar

$ exit
```

Then, just copy the aar out of the container:

```console
$ docker cp mediapipe:/mediapipe/bazel-bin/mediapipe/examples/android/src/java/com/google/mediapipe/apps/handtracking_aar/mp_hand_tracking_aar.aar ./mp_hand_tracking_aar.aar
```

And to cleanup, you can remove the container:

```console
$ docker container rm mediapipe
```
