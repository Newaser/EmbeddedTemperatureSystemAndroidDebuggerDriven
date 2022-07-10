package com.verisilicon.sample.util;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class Utils {
    public static final int PERMISSIONS_REQUEST_CODE = 10;
    /**
     * All permissions app need
     */
    private static final String[] appRuntimePermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    /**
     * TIPS: BLE requires system location function
     * <p>
     * Check whether the system location function is on
     *
     * @param context context
     * @return GPS on state
     */
    public static boolean isLocationEnabled(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return (gps || network);
    }

    /**
     * Turn on system location function,
     *
     * @param context context
     */
    public static void enableLocation(final Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(settingsIntent);
    }

    public static boolean isBTEnabled() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter == null) return false;
        return adapter.isEnabled();
    }

    /**
     * Enable Bluetooth
     *
     * @return true: result will notify at broadcast {@link BluetoothAdapter#ACTION_REQUEST_ENABLE}
     * false: not support BT or open failed.
     */
    public static boolean enableBT() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter == null) return false;
        if (!adapter.isEnabled()) return adapter.enable();
        return true;
    }

    /**
     * Check if permissions {@link #appRuntimePermissions} granted,
     * and return the arrays of permissions not granted.
     *
     * @param context {@link Context}
     * @return permissions not granted.
     */
    public static String[] checkAppRuntimePermission(@NonNull Context context) {
        List<String> failedPermission = new ArrayList<>();
        String[] failedPermissionArray;

        for (String permission : appRuntimePermissions) {
            if (context.checkCallingOrSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) {
                failedPermission.add(permission);
            }
        }
        failedPermissionArray = new String[failedPermission.size()];
        return failedPermission.toArray(failedPermissionArray);
    }

    /**
     * Request permissions needed,
     * please see result at {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     *
     * @param activity {@link Activity}
     */
    public static void requestAppRuntimePermission(@NonNull Activity activity) {
        String[] permissionLeft = checkAppRuntimePermission(activity);

        if (permissionLeft.length > 0)
            ActivityCompat.requestPermissions(activity, permissionLeft, PERMISSIONS_REQUEST_CODE);
    }
}
