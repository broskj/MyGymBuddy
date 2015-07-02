package com.broskj.mygymbuddy;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by Kyle on 7/2/2015.
 */
public class Counter extends CountDownTimer {
    /**
     * @param millisInFuture    The number of millis in the future from the call
     * to {@link #start()} until the countdown is done and {@link #onFinish()}
     * is called.
     * @param countDownInterval The interval along the way to receive
     * {@link #onTick(long)} callbacks.
     */
    TextView textView;

    public Counter(TextView textView, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.textView = textView;
    }//end constructor

    @Override
    public void onTick(long millis) {
        textView.setText(formatTime(millis));
    }//end onTick

    @Override
    public void onFinish() {
        textView.setText("00:00;00");
        //make noise
    }//end onFinish

    public String formatTime(long millis) {
        return String.format("%02d:%02d;02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }//end formatTime
}//end class Counter
