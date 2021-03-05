package com.handapp.mediapipebluetooth;

import mikera.vectorz.Vector3;

class Average {

    static float getAvg(float prev_avg, float x, int n)
    {
        return (prev_avg * n + x) / (n + 1);
    }

    static Vector3 streamAvg(Vector3[] arr, int n)
    {
        float avgX = 0;
        float avgY = 0;
        float avgZ = 0;

        Vector3 avgVec = null;

        for (int i = 0; i < n; i++)
        {
            avgX = getAvg(avgX, (float)arr[i].getX(), i);
            avgY = getAvg(avgY, (float)arr[i].getY(), i);
            avgZ = getAvg(avgZ, (float)arr[i].getZ(), i);
        }
        avgVec = Vector3.of(avgX, avgY, avgZ);
        return avgVec;
    }

    //add to list
}
