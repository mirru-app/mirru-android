package com.handapp.mediapipebluetooth;

import java.text.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class FindCircleTest
{
    double r;
    DecimalFormat df;
    int h;
    int k;

    @Test
    public void main () {
        findCircle(1, 1, 2, 4, 5, 3);
        assertEquals(2.23607, r, 1);
        assertEquals(3, h, 0);
        assertEquals(2, k, 0);
    }

    // Function to find the circle on
    // which the given three points lie
    public void findCircle(int x1, int y1,
                           int x2, int y2,
                           int x3, int y3) {
        int x12 = x1 - x2;
        int x13 = x1 - x3;

        int y12 = y1 - y2;
        int y13 = y1 - y3;

        int y31 = y3 - y1;
        int y21 = y2 - y1;

        int x31 = x3 - x1;
        int x21 = x2 - x1;

        // x1^2 - x3^2
        int sx13 = (int)(Math.pow(x1, 2) -
                Math.pow(x3, 2));

        // y1^2 - y3^2
        int sy13 = (int)(Math.pow(y1, 2) -
                Math.pow(y3, 2));

        int sx21 = (int)(Math.pow(x2, 2) -
                Math.pow(x1, 2));

        int sy21 = (int)(Math.pow(y2, 2) -
                Math.pow(y1, 2));

        int f = ((sx13) * (x12)
                + (sy13) * (x12)
                + (sx21) * (x13)
                + (sy21) * (x13))
                / (2 * ((y31) * (x12) - (y21) * (x13)));
        int g = ((sx13) * (y12)
                + (sy13) * (y12)
                + (sx21) * (y13)
                + (sy21) * (y13))
                / (2 * ((x31) * (y12) - (x21) * (y13)));

        int c = -(int)Math.pow(x1, 2) - (int)Math.pow(y1, 2) -
                2 * g * x1 - 2 * f * y1;

        // eqn of circle be x^2 + y^2 + 2*g*x + 2*f*y + c = 0
        // where centre is (h = -g, k = -f) and radius r
        // as r^2 = h^2 + k^2 - c
        h = -g;
        k = -f;
        int sqr_of_r = h * h + k * k - c;

        // r is the radius
        r = Math.sqrt(sqr_of_r);
        df = new DecimalFormat("#.#####");

        System.out.println("Centre = (" + h + "," + k + ")");
        System.out.println("Radius = " + df.format(r));
    }
}

// This code is contributed by chandan_jnu
// https://www.geeksforgeeks.org/equation-of-circle-when-three-points-on-the-circle-are-given/