
package com.handapp.mediapipebluetooth;

import java.text.*;
import mikera.vectorz.Vector2;

class FingerCircles
{
    // Function to find the circle on
    // which the given three points lie
    // This code is contributed by chandan_jnu
    //https://www.geeksforgeeks.org/equation-of-circle-when-three-points-on-the-circle-are-given/
    static double getAngle(float x1, float y1, float x2, float y2, float x3, float y3) {
        float x12 = x1 - x2;
        float x13 = x1 - x3;

        float y12 = y1 - y2;
        float y13 = y1 - y3;

        float y31 = y3 - y1;
        float y21 = y2 - y1;

        float x31 = x3 - x1;
        float x21 = x2 - x1;

        // x1^2 - x3^2
        float sx13 = (float)(Math.pow(x1, 2) -
                Math.pow(x3, 2));

        // y1^2 - y3^2
        float sy13 = (float)(Math.pow(y1, 2) -
                Math.pow(y3, 2));

        float sx21 = (float)(Math.pow(x2, 2) -
                Math.pow(x1, 2));

        float sy21 = (float)(Math.pow(y2, 2) -
                Math.pow(y1, 2));

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

        float c = -(float)Math.pow(x1, 2) - (float)Math.pow(y1, 2) -
                2 * g * x1 - 2 * f * y1;

        // eqn of circle be x^2 + y^2 + 2*g*x + 2*f*y + c = 0
        // where centre is (h = -g, k = -f) and radius r
        // as r^2 = h^2 + k^2 - c
        float h = -g;
        float k = -f;
        float sqr_of_r = h * h + k * k - c;

        // r is the radius
        double r = Math.sqrt(sqr_of_r);
        DecimalFormat df = new DecimalFormat("#.#####");
        System.out.println("Centre = (" + h + "," + k + ")");
        System.out.println("Radius = " + df.format(r));

        //normalize radii
        final double PI = 3.14;
        double shift = 0.020746529414226417;
        double normalizedRadii = Math.atan(1 * r - shift) / ( PI / 2) * 1;
        double angle = normalizedRadii * 180;
        return normalizedRadii;
    }
}