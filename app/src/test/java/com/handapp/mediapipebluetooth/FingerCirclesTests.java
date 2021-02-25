package com.handapp.mediapipebluetooth;

import java.text.*;
import java.util.List;

import org.junit.Test;
import com.handapp.mediapipebluetooth.FingerCircles;
import static org.junit.Assert.*;
import mikera.vectorz.Vector2;
import mikera.vectorz.Vector3;

public class FingerCirclesTests {
    static DecimalFormat df;
    static double r;
    static float h;
    static float k;

    @Test
    public void testRotatePoints() {
        // p1 -> (-1, 0, 0)
        // p2 -> (0, 1, 0)
        // p3 -> (1, 0, 0)
        // normal -> (1, 0, 0)

        // rp1 -> (0, 0, 1)
        // rp2 -> (0, 1, 0)
        // rp3 -> (0, 0, -1)

        List rotated = FingerCircles.rotatePoints(Vector3.of(1, 0, 0), Vector3.of(-1, 0, 0), Vector3.of(0, 1, 0), Vector3.of(0,0,-1));
        Vector3[] rotatedPoints = (Vector3[])rotated.get(1);
        Vector3[] expectedArray = new Vector3[] {Vector3.of(0,0,1), Vector3.of(0,1,0), Vector3.of(0,0,-1)};
        assertArrayEquals(expectedArray, rotatedPoints);
    }
}