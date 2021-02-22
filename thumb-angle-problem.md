The index, middle, and ring fingers move with a servo that moves
at an angle from 0 to 180 degrees. 0 means the finger is full upright and
180 means the fingers are fully flexed down.

To calculate the movement of the fingers, we first calculate the direction vector of the fingers 
by subtracting the 3d point at the base of the finger and the 3d point at the tip of the finger.

![finger direction](finger-direction.png)

Then we calculate the palm normal by getting the three points in the palm, 
calculating the vectors for side 1 and side 2, and getting the cross product
of those vectors.

![palm normal](palm-normal.png)

Finally we compute the angle of the finger direction and the palm normal in the 
servoAngle method. This returns the angle in radians and we calculate the degrees
with that.

![angle](angle.png)

Once we have the estimated angle, there is this calculation which is then done on the angle to fine-tune it. It was taken
from someone else's project and we aren't sure what it means. It seems to work well. Its the following:
  `double servoAngle = (160 - (100 - angle_degrees) * 1.5)`

The same developer, for the thumb specifically, does the following:
  `double servoAngle = (20+(100-fingerAngle)*1.5);; // EMPIRICAL CONVERSION, MAY BE DIFFERENT FOR DIFFERENT SERVOS!`

However, they are testing with a more simplistic prosthetic and we need something more precise.

Problem: How can we calculate the movement of the thumb to move precisely for different grips? The thumb on the prosthetic is
only able to move in and out and stops at a full 180 at the middle finger.

![thumb](thumb.png)


```
    private String getMultiHandLandmarksDebugString(List<NormalizedLandmarkList> multiHandLandmarks) {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand landmarks";
        }
        String multiHandLandmarksStr = "";
        int handIndex = 0;
        for (NormalizedLandmarkList landmarks : multiHandLandmarks) {
            int landmarkIndex = 0;
            for (NormalizedLandmark landmark : landmarks.getLandmarkList()) {

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
                ++landmarkIndex;
            }
            ++handIndex;
        }
        return multiHandLandmarksStr;
    }

    private String getAnglesOfFingersString(List<NormalizedLandmarkList> multiHandLandmarks) {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand landmarks";
        }
        String fingerValuesString = null;
        int handIndex = 0;

        Vector3 palm0 = null;
        Vector3 palm5 = null;
        Vector3 palm17 = null;;

        Vector3 thumb1 = null;
        Vector3 thumb3 = null;
        Vector3 index5 = null;
        Vector3 index8 = null;
        Vector3 mid9 = null;
        Vector3 mid12 = null;
        Vector3 ring13 = null;
        Vector3 ring16 = null;

        for (NormalizedLandmarkList landmarks : multiHandLandmarks)  {
            int landmarkIndex = 0;
            for (NormalizedLandmark landmark : landmarks.getLandmarkList()) {
                if (landmarkIndex == 0) {
                    palm0 = Vector3.of(landmark.getX(), landmark.getY(), landmark.getZ());
                }

                if (landmarkIndex == 1) {
                    thumb1 = Vector3.of(landmark.getX(), landmark.getY(), landmark.getZ());
                }

                if (landmarkIndex == 3) {
                    thumb3 = Vector3.of(landmark.getX(), landmark.getY(), landmark.getZ());
                }

                if (landmarkIndex == 5) {
                    palm5 = Vector3.of(landmark.getX(), landmark.getY(), landmark.getZ());
                    index5 = Vector3.of(landmark.getX(), landmark.getY(), landmark.getZ());
                }

                if (landmarkIndex == 8) {
                    index8 = Vector3.of(landmark.getX(), landmark.getY(), landmark.getZ());
                }

                if (landmarkIndex == 9) {
                    mid9 = Vector3.of(landmark.getX(), landmark.getY(), landmark.getZ());
                }

                if (landmarkIndex == 12) {
                    mid12 = Vector3.of(landmark.getX(), landmark.getY(), landmark.getZ());
                }

                if (landmarkIndex == 13) {
                    ring13 = Vector3.of(landmark.getX(), landmark.getY(), landmark.getZ());
                }

                if (landmarkIndex == 16) {
                    ring16 = Vector3.of(landmark.getX(), landmark.getY(), landmark.getZ());
                }

                if (landmarkIndex == 17) {
                    palm17 = Vector3.of(landmark.getX(), landmark.getY(), landmark.getZ());
                }

                ++landmarkIndex;
            }

            Vector3 PalmNormal = calcPalmNormal(palm0, palm5, palm17);

            double thumbAngle = servoAngle(fingerDir(palm0, thumb3), PalmNormal, true);
            double indexAngle = servoAngle(fingerDir(index5, index8), PalmNormal, false);
            double midAngle = servoAngle(fingerDir(mid9, mid12), PalmNormal, false);
            double ringAngle = servoAngle(fingerDir(ring13, ring16), PalmNormal, false);

            fingerValuesString = (int)thumbAngle + "," + (int)indexAngle + "," + (int)midAngle + "," + (int)ringAngle;
            ++handIndex;
        }
        return fingerValuesString;
    }

    private Vector3 fingerDir(Vector3 startingPoint, Vector3 terminalPoint) {
        terminalPoint.sub(startingPoint);
        Vector3 direction = new Vector3(terminalPoint);
        direction.toNormal();
        return direction;
    }

    private Vector3 calcPalmNormal(Vector3 palm0, Vector3 palm5, Vector3 palm17) {
        palm5.sub(palm0);
        Vector3 side1 = palm5;

        palm17.sub(palm0);
        Vector3 side2 = palm17;

        side1.crossProduct(side2);

        Vector3 palmNormal = new Vector3(side1.toNormal());
        return palmNormal;
    }

    private double servoAngle(Vector3 fingerDir, Vector3 palmNormal, boolean isThumb) {
        //angle calculation by:
        //https://www.instructables.com/Robotic-Hand-controlled-by-Gesture-with-Arduino-Le/
        double scalarProduct = palmNormal.x * fingerDir.x + palmNormal.y * fingerDir.y + palmNormal.z * fingerDir.z;
        double palm_module = Math.sqrt(palmNormal.x * palmNormal.x + palmNormal.y * palmNormal.y + palmNormal.z * palmNormal.z);
        double finger_module = Math.sqrt(fingerDir.x * fingerDir.x + fingerDir.y * fingerDir.y + fingerDir.z * fingerDir.z);
        double angle_radians = Math.acos(scalarProduct / (palm_module * finger_module));
        double angle_degrees = angle_radians * 180 / Math.PI;

        double servoAngle = angle_degrees;
        if (!isThumb) {
            servoAngle = (160 - (100 - angle_degrees) * 1.5); // EMPIRICAL CONVERSION, MAY BE DIFFERENT FOR DIFFERENT SERVOS!
        } else {
            servoAngle = (20+(100-angle_degrees)*1.5);; // EMPIRICAL CONVERSION, MAY BE DIFFERENT FOR DIFFERENT SERVOS
        }

        if(servoAngle < 1)
            servoAngle = 1;
        else if (servoAngle > 180)
            servoAngle = 180;
        return angle_degrees;
    }
}
```