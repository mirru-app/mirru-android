package com.handapp.mediapipebluetooth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CountDownFragment extends Fragment {
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long startTimeInMilliseconds = 11000; //10 seconds
    private ToggleButton toggleButton;
    private TextView countDownText;

    public CountDownFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.countdown_fragment, container, false);
        countDownText = view.findViewById(R.id.countdown_text);
        toggleButton = view.findViewById(R.id.toggleButton);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    StartTimer();
                } else if (!toggleButton.isChecked()){
                    StopTimer();
                }
            }
        });
        return view;
    }

    public void StartTimer() {
        countDownTimer = new CountDownTimer(startTimeInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDownText.setText("" + millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                StopTimer();
            }
        }.start();
        countDownText.setText(startTimeInMilliseconds/1000 + "");
        isTimerRunning = true;
    }

    public void StopTimer() {
        countDownTimer.cancel();
        toggleButton.setChecked(false);
        isTimerRunning = false;
        countDownText.setText("");
    }
}