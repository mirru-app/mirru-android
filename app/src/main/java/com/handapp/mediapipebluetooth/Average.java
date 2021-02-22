package com.handapp.mediapipebluetooth;

class Average {

    static float getAvg(float prev_avg, float x, int n)
    {
        return (prev_avg * n + x) / (n + 1);
    }

    static float streamAvg(Float[] arr, int n)
    {
        float avg = 0;
        for (int i = 0; i < n; i++)
        {
            avg = getAvg(avg, arr[i], i);
        }
        return avg;
    }
}