package com.handapp.mediapipebluetooth;

import mikera.vectorz.Vector2;

class Average {

    // Returns the new average after including x
    static float getAvg(float prev_avg, float x, int n)
    {
        return (prev_avg * n + x) / (n + 1);
    }

    // Prints average of a stream of numbers
    static float streamAvg(Float[] arr, int n)
    {
        float avg = 0;
        Vector2 avgVec = null;
        for (int i = 0; i < n; i++)
        {
            avg = getAvg(avg, arr[i], i);
        }
        return avg;
    }
}

// This code is contributed by Smitha Dinesh Semwal
