package com.verisilicon.sample.presenters;

import android.app.Activity;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import com.verisilicon.protocol.ble.GattAttributes;
import com.verisilicon.vsbleclient.BTScanner;
import com.verisilicon.vsbleclient.device.ScanDevice;
import com.verisilicon.vsbleclient.proxy.DeviceProxy;
import com.verisilicon.vsbleclient.proxy.ProxyListeners;
import com.verisilicon.vsbleclient.proxy.ProxyManager;

import androidx.annotation.NonNull;

public class ConnectionPresenter extends BaseEnvironmentPresenter {
    protected ProxyManager proxyManager;
    protected DeviceProxy deviceProxy;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final ProxyListeners.ConnectionChangeListener connectionChangeListener =
            (proxy, connect) -> {
        Log.d(TAG, "Connection presenter connected: " + connect);
        mainHandler.post(() -> {
            if (connect)
                this.onConnected(proxy);
            else
                this.onDisconnected(proxy);
        });
    };

    public ConnectionPresenter(@NonNull Activity activity) {
        super(activity);
        ProxyManager.getInstance().init(activity);
        proxyManager = ProxyManager.getInstance();
    }

    /**
     * @param name 这里和Demo UI有关，正常应该是传入蓝牙的地址
     */
    public void connect(@NonNull String name) {
        //VeriSilicon 公司特有的Service,可以用来筛选扫描设备，可选
        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(ParcelUuid.fromString(GattAttributes.Services.VERISILICON_SERVICE)).build();

        // Step 1 扫描设备
        proxyManager.getScanner().scan(ScanSettings.SCAN_MODE_BALANCED,
                filter, new BTScanner.ScanResultListener() {
                    @Override
                    public void onScanResult(ScanDevice device) {
                        // Step 2 筛选设备并连接
                        if (name.equals(device.getScanResult().getDevice().getName())) {
                            DeviceProxy deviceProxy;

                            // ProxyManager 开始代理当前设备
                            deviceProxy = proxyManager.createProxy(device);
                            proxyManager.switchProxy(deviceProxy);
                            ConnectionPresenter.this.deviceProxy = deviceProxy;
                            deviceProxy.addConnectionChangedListener(connectionChangeListener);

                            // 开始连接流程
                            deviceProxy.start();
                            showLog("[ConnectionPresenter] getDeviceName11: " + deviceProxy.getDeviceName());
                        }
                    }

                    @Override
                    public void failed(int error) {
                        showLog("Scan failed:" + error);
                    }
                });
    }

    public void disConnect() {
        if (deviceProxy != null)
            deviceProxy.stop();
    }


    @Override
    protected void onAppEnvError(int errorMask) {
        super.onAppEnvError(errorMask);
        showLog("App Environment Error:" + errorMask);
    }

    @Override
    protected void onAppEnvOK() {
        super.onAppEnvOK();
        if (proxyManager != null)
            proxyManager.getScanner().updateAdapter();

        showLog("App Environment OK");
    }

    protected void onConnected(DeviceProxy proxy) {

    }

    protected void onDisconnected(DeviceProxy proxy) {

    }

    protected void showLog(String message) {
        Log.d(TAG, "showLog: " + message);
    }
}