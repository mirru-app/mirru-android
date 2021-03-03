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
    public void testSamePoints() {
        Vector3 rp1 = Vector3.of(-1, 0, 0);
        Vector3 rp2 = Vector3.of(0, 1, 0);
        Vector3 rp3 = Vector3.of(1, 0, 0);
        Vector3[] expected = {rp1, rp2, rp3};

        List rotated = FingerCircles.rotatePoints(rp1, rp2, rp3);
        Vector3[] rotatedPoints = (Vector3[])rotated.get(1);

        assertArrayEquals(expected, rotatedPoints);
    }

    @Test
    public void testRotatePoints() {
        // p1 = [0, 0, -1]
        // p2 = [0, 1, 0]
        // p3 = [0, 0 1]

        // rp1=[1. 0. 0.]
        // rp2=[0. 1. 0.]
        // rp3=[-1.  0.  0.]

        Vector3 rp1 = Vector3.of(0, 0, -1);
        Vector3 rp2 = Vector3.of(0, 1, 0);
        Vector3 rp3 = Vector3.of(0, 0, 1);

        Vector3 ex1 = Vector3.of(1, 0, 0);
        Vector3 ex2 = Vector3.of(0, 1, 0);
        Vector3 ex3 = Vector3.of(-1, 0, 0);
        Vector3[] expected = {ex1, ex2, ex3};

        List rotated = FingerCircles.rotatePoints(rp1, rp2, rp3);
        Vector3[] rotatedPoints = (Vector3[])rotated.get(1);

        assertArrayEquals(expected, rotatedPoints);
    }
}