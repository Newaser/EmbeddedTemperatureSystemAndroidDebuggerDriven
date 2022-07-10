package com.verisilicon.sample.views;

import com.verisilicon.vsbleclient.proxy.DeviceProxy;

/** The interface formed view of test case MVP */
public interface ITestCaseView {

    /** the variety occurs on the screen once connected */
    void onConnected(DeviceProxy deviceProxy);

    /** the variety occurs on the screen once disconnected */
    void onDisconnected(DeviceProxy deviceProxy);

    /** draw a message onto the screen */
    void drawLog(String message);

    /** update the display of the dial */
    void updateDialDisplay(double temperature);
}
