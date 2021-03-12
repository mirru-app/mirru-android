package com.handapp.mediapipebluetooth;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CountDownFragment extends Fragment {
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long startTimeInMilliseconds; //10 seconds
    private ToggleButton toggleButton;
    private TextView countDownText;

    public CountDownFragment() {
        // Required empty public constructor
    }

    public interface CountdownInterface {
        void sendCountdownState(boolean isTimerRunning);
    }

    CountdownInterface countDownInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    RadioGroup radioGroup;
    RadioButton radioButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.countdown_fragment, container, false);

        radioGroup = view.findViewById(R.id.radioGroup);
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
                countDownInterface.sendCountdownState(isTimerRunning);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) view.findViewById(checkedId);
                switch(rb.getText().toString()) {
                    case "10s":
                        startTimeInMilliseconds = 11000;
                        break;
                    case "20s":
                        startTimeInMilliseconds = 21000;
                        break;
                    case "infinite":
                        startTimeInMilliseconds = 990000;
                        break;
                }
                Log.w("w", "startTimeInMilliseconds " + startTimeInMilliseconds);
                Log.w("w", "You Selected " + rb.getText());
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CountdownInterface) {
            countDownInterface = (CountdownInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement CountDownInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        countDownInterface = null;
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
        countDownInterface.sendCountdownState(isTimerRunning);
    }
}