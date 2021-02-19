package com.handapp.mediapipebluetooth;

import java.text.*;
import org.junit.Test;
import static org.junit.Assert.*;
import mikera.vectorz.Vector2;

public class FindCircleTest {
    static DecimalFormat df;
    static double r;
    static float h;
    static float k;

    @Test
    public void main() {
        findCircle(Vector2.of(-0.1, 0.2), Vector2.of(-0.2, 0.1), Vector2.of(0.003, 3.0));
        assertEquals(2.14, r, 1);
        assertEquals(-1.66, h, 1);
        assertEquals(1.66, k, 1);
    }

    static void findCircle(Vector2 point1, Vector2 point2, Vector2 point3)
    {
        // Function to find the circle on
        // which the given three points lie
        float x12 = (float) point1.x - (float) point2.x;
        float x13 = (float) point1.x - (float) point3.x;

        float y12 = (float) point1.y - (float) point2.y;
        float y13 = (float) point1.y - (float) point3.y;

        float y31 = (float) point3.y - (float) point1.y;
        float y21 = (float) point2.y - (float) point1.y;

        float x31 = (float) point3.x - (float) point1.x;
        float x21 = (float) point2.x - (float) point1.x;

        // x1^2 - x3^2
        float sx13 = (float) (Math.pow(point1.x, 2) -
                Math.pow(point3.x, 2));

        // y1^2 - y3^2
        float sy13 = (float) (Math.pow(point1.y, 2) -
                Math.pow(point3.y, 2));

        float sx21 = (float) (Math.pow(point2.x, 2) -
                Math.pow(point1.x, 2));

        float sy21 = (float) (Math.pow(point2.y, 2) -
                Math.pow(point1.y, 2));

        float f = ((sx13) * (x12)
                + (sy13) * (x12)
                + (sx21) * (x13)
                + (sy21) * (x13))
                / (2 * ((y31) * (x12) - (y21) * (x13)));
        float g = ((sx13) * (y12)
                + (sy13) * (y12)
                + (sx21) * (y13)
                + (sy21) * (y13))
                / (2 * ((x31) * (y12) - (x21) * (y13)));

        float c = -(float) Math.pow((float) point1.x, 2) - (float) Math.pow((float) point1.y, 2) -
                2 * g * (float) point1.x - 2 * f * (float) point1.y;

        // eqn of circle be x^2 + y^2 + 2*g*x + 2*f*y + c = 0
        // where centre is (h = -g, k = -f) and radius r
        // as r^2 = h^2 + k^2 - c
        h = -g;
        k = -f;
        float sqr_of_r = h * h + k * k - c;

        // r is the radius
        r = Math.sqrt(sqr_of_r);
        df = new DecimalFormat("#.#####");
        System.out.println("Centre = ("+h +","+k +")");
        System.out.println("Radius = "+df.format(r));
    }
}

// This code is contributed by chandan_jnu
// https://www.geeksforgeeks.org/equation-of-circle-when-three-points-on-the-circle-are-given/