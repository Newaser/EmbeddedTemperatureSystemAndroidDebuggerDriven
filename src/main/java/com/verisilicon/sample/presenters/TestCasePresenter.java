package com.verisilicon.sample.presenters;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.verisilicon.sample.cases.UseCase;
import com.verisilicon.sample.views.ITestCaseView;
import com.verisilicon.vsbleclient.proxy.DeviceProxy;

import androidx.annotation.NonNull;

public class TestCasePresenter extends ConnectionPresenter {
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ITestCaseView iView;

    public TestCasePresenter(@NonNull Activity activity, ITestCaseView iView) {
        super(activity);
        this.iView = iView;
    }

    @Override
    protected void onConnected(DeviceProxy proxy) {
        super.onConnected(proxy);

        // presenter controls i-view
        if (iView != null)
            iView.onConnected(proxy);

        // run demo code once every 3 secs
        mainHandler.postDelayed(this::enterDemoCode, 3000);
    }

    @Override
    protected void onDisconnected(DeviceProxy proxy) {
        super.onDisconnected(proxy);

        // presenter controls i-view
        if (iView != null)
            iView.onDisconnected(proxy);
    }

    @Override
    protected void showLog(String message) {
        super.showLog(message);
        if (iView != null) {
            mainHandler.post(() -> iView.drawLog(message));
        }
    }

    protected void updateDial(double temperature) {
        if (iView != null) {
            mainHandler.post(() -> iView.updateDialDisplay(temperature));
        }
    }

    /** demo code that receive temperature value */
    private void enterDemoCode() {
        UseCase useCase = new UseCase(this.deviceProxy);

        useCase.setCaseListener(this::showLog);
        useCase.setValueListener(this::updateDial);
        useCase.begin();
    }
}
