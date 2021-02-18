package com.handapp.mediapipebluetooth;

import mikera.arrayz.Array;
import mikera.arrayz.Arrayz;
import mikera.arrayz.INDArray;
import mikera.arrayz.NDArray;
import mikera.matrixx.Matrix;
import mikera.matrixx.algo.Determinant;
import mikera.matrixx.impl.IdentityMatrix;
import mikera.matrixx.impl.TransposedMatrix;
import mikera.matrixx.impl.VectorMatrixM3;
import mikera.vectorz.Vector;
import mikera.vectorz.Vector3;


public class FingerAngles {
    public static Vector3 fingerDir(Vector3 startingPoint, Vector3 terminalPoint) {
        terminalPoint.sub(startingPoint);
        Vector3 direction = new Vector3(terminalPoint);
        direction.toNormal();
        return direction;
    }

    public static Vector3 calcPalmNormal(Vector3 palm0, Vector3 palm5, Vector3 palm17) {
        palm5.sub(palm0);
        Vector3 side1 = palm5;

        palm17.sub(palm0);
        Vector3 side2 = palm17;

        side1.crossProduct(side2);

        Vector3 palmNormal = new Vector3(side1.toNormal());
        return palmNormal;
    }

    public static double servoAngle(Vector3 fingerDir, Vector3 palmNormal, boolean isThumb) {
        //angle calculation by:
        //https://www.instructables.com/Robotic-Hand-controlled-by-Gesture-with-Arduino-Le/
        double scalarProduct = palmNormal.x * fingerDir.x + palmNormal.y * fingerDir.y + palmNormal.z * fingerDir.z;
        double palm_module = Math.sqrt(palmNormal.x * palmNormal.x + palmNormal.y * palmNormal.y + palmNormal.z * palmNormal.z);
        double finger_module = Math.sqrt(fingerDir.x * fingerDir.x + fingerDir.y * fingerDir.y + fingerDir.z * fingerDir.z);
        double angle_radians = Math.acos(scalarProduct / (palm_module * finger_module));
        double angle_degrees = angle_radians * 180 / Math.PI;

        double servoAngle;
        if (!isThumb) {
            servoAngle = (160 - (100 - angle_degrees) * 1.5); // EMPIRICAL CONVERSION, MAY BE DIFFERENT FOR DIFFERENT SERVOS!
        } else {
            servoAngle = (20+(100-angle_degrees)*1.5);; // EMPIRICAL CONVERSION, MAY BE DIFFERENT FOR DIFFERENT SERVOS
        }

        if(servoAngle < 1)
            servoAngle = 1;
        else if (servoAngle > 180)
            servoAngle = 180;

        return servoAngle;
    }

    public double map(double value, double start1, double stop1, double start2, double stop2) {
        double mappedValue = (value - start1) / (stop1 - start1) * (stop2 - start2) + start2;
        return mappedValue;
    }
}
