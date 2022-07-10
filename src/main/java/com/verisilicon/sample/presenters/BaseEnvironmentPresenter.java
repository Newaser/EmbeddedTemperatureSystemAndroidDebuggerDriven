package com.verisilicon.sample.presenters;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.util.Log;

import com.verisilicon.sample.util.Utils;

import androidx.annotation.NonNull;

/**
 * This presenter will check all environments that VeriSilicon libs needed.
 */
public class BaseEnvironmentPresenter {
    public static final String TAG = "VSLog";
    public static final int ERROR_BT = 1;
    public static final int ERROR_LOCATION = 1 << 1;
    public static final int ERROR_PERMISSION = 1 << 2;

    private static final IntentFilter filter = new IntentFilter();

    static {
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(LocationManager.MODE_CHANGED_ACTION);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    private int errorFlag = -1;
    private Activity activity;

    public BaseEnvironmentPresenter(@NonNull Activity activity) {
        init(activity);
    }

    private void init(Activity activity) {
        this.activity = activity;
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case LocationManager.PROVIDERS_CHANGED_ACTION:
                    case LocationManager.MODE_CHANGED_ACTION:
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        checkRuntimeEnvironment(activity);
                        break;
                    default:
                        break;
                }
            }
        };
        activity.registerReceiver(receiver, filter);
        checkRuntimeEnvironment(activity);
    }

    private void checkRuntimeEnvironment(Activity activity) {
        int errorFlag = 0;

        // pre-check
        boolean isBTEnable = Utils.isBTEnabled();
        boolean isLocationEnable = Utils.isLocationEnabled(activity);
        boolean isPermissionGranted = Utils.checkAppRuntimePermission(activity).length == 0;

        if (!isBTEnable)
            Utils.enableBT();

        if (!isLocationEnable)
            Utils.enableLocation(activity);

        if (!isPermissionGranted)
            Utils.requestAppRuntimePermission(activity);

        // check again
        isBTEnable = Utils.isBTEnabled();
        isLocationEnable = Utils.isLocationEnabled(activity);
        isPermissionGranted = Utils.checkAppRuntimePermission(activity).length == 0;

        if (!isBTEnable)
            errorFlag |= ERROR_BT;

        if (!isLocationEnable)
            errorFlag |= ERROR_LOCATION;

        if (!isPermissionGranted)
            errorFlag |= ERROR_PERMISSION;

        if (this.errorFlag == errorFlag)
            return;

        this.errorFlag = errorFlag;
        if (errorFlag == 0) {
            onAppEnvOK();
        } else {
            onAppEnvError(errorFlag);
        }
    }

    public void refreshPermission() {
        checkRuntimeEnvironment(activity);
    }

    protected void onAppEnvOK() {
        Log.d(TAG, "onAppEnvOK: ");
    }

    protected void onAppEnvError(int errorMask) {
        Log.e(TAG, "onAppEnvError: " + errorMask);
    }
}
