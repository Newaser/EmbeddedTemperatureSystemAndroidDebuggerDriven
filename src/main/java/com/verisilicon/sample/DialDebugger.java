package com.verisilicon.sample;

import android.app.Activity;
import android.webkit.WebView;

import com.verisilicon.sample.views.ITestCaseView;

import java.util.Timer;
import java.util.TimerTask;

class DialDebugger extends TimerTask {
    Activity context;
    ITestCaseView iView;
    Timer timer;
    int interval;
    WebView wv;

    public DialDebugger(Activity context, ITestCaseView iView, int interval, WebView wv) {
        this.context = context;
        this.iView = iView;
        this.wv = wv;
        this.interval = interval;
    }

    public void start() {
        timer = new Timer();
        // execute the first task after milliseconds
        timer.schedule(this, interval, interval);
    }

    @Override
    public void run() {
        if (context == null || context.isFinishing()) {  // Activity killed
            this.cancel();
            return;
        }

        context.runOnUiThread(() -> {
            double temperature = fixed(28 + Math.random() * 2, 2);
            iView.drawLog("Temperature: " + temperature);
            iView.updateDialDisplay(temperature);
        });
    }

    /** fix a double to specific decimal digits */
    private double fixed(double num, int digits) {
        int k = (int) Math.pow(10, digits);
        return (double) Math.round(num * k) / k;
    }
}