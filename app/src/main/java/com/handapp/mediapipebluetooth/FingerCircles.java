package com.handapp.mediapipebluetooth;

import java.text.*;
import mikera.vectorz.Vector2;

class FingerCircles
{
    double angle;

    // Function to find the circle on
    // which the given three points lie
    // This code is contributed by chandan_jnu

    static double getAngle(Vector2 point1, Vector2 point2, Vector2 point3, boolean isThumb) {
        int x12 = (int) point1.x - (int) point2.x;
        int x13 = (int) point1.x - (int) point3.x;

        int y12 = (int) point1.y - (int) point2.y;
        int y13 = (int) point1.y - (int) point3.y;

        int y31 = (int) point3.y - (int) point1.y;
        int y21 = (int) point2.y - (int) point1.y;

        int x31 = (int) point3.x - (int) point1.x;
        int x21 = (int) point2.x - (int) point1.x;

        // x1^2 - x3^2
        int sx13 = (int) (Math.pow(point1.x, 2) -
                Math.pow(point3.x, 2));

        // y1^2 - y3^2
        int sy13 = (int) (Math.pow(point1.y, 2) -
                Math.pow(point3.y, 2));

        int sx21 = (int) (Math.pow(point2.x, 2) -
                Math.pow(point1.x, 2));

        int sy21 = (int) (Math.pow(point2.y, 2) -
                Math.pow(point1.y, 2));

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

        int c = -(int) Math.pow((int) point1.x, 2) - (int) Math.pow((int) point1.y, 2) -
                2 * g * (int) point1.x - 2 * f * (int) point1.y;

        // eqn of circle be x^2 + y^2 + 2*g*x + 2*f*y + c = 0
        // where centre is (h = -g, k = -f) and radius r
        // as r^2 = h^2 + k^2 - c
        int h = -g;
        int k = -f;
        int sqr_of_r = h * h + k * k - c;

        // r is the radius
        double r = Math.sqrt(sqr_of_r);
        DecimalFormat df = new DecimalFormat("#.#####");
        Vector2 center = Vector2.of(h, k);

        //normalize radii
        final double PI = 3.14;
        double shift = 0.020746529414226417;
        double normalizedRadii = Math.atan(1 * r - shift) / ( PI / 2) * 1;
        double angle = normalizedRadii * 180;
        return angle;
    }
}