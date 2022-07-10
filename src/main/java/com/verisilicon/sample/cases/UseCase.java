package com.verisilicon.sample.cases;

import com.verisilicon.sample.cases.protocol.CustomParam;
import com.verisilicon.vsbleclient.device.Callback;
import com.verisilicon.vsbleclient.proxy.DeviceProxy;

public class UseCase implements ICase {
    private final DeviceProxy deviceProxy;
    private CaseListener caseListener;
    private ValueListener valueListener;

    public UseCase(DeviceProxy deviceProxy) {
        this.deviceProxy = deviceProxy;
    }

    public void begin() {
        showAPIUsage();
    }

    private void showAPIUsage() {
        caseAddListeners();
        caseWriteToDevice();
    }

    private void caseWriteToDevice() {
        deviceProxy.sendVSRequest(new CustomParam((byte) 0xFF, 0xFFFFFFF), new Callback<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                printLog("Write custom message success");
            }

            @Override
            public void onError() {
                printLog("Error: Write custom message");
            }
        });
    }

    private void caseAddListeners() {
        deviceProxy.addExtraVSProtocolListener(protocolBase -> {
            if (protocolBase.eventCode == CustomParam.CUSTOM_PARAM_EVENT_CODE) {
                CustomParam customParam = new CustomParam(protocolBase);

                customParam.unpack();

                // if data type is temperature, then output
                if(customParam.getType() == CustomParam.TEMPERATURE_TYPE) {
                    printLog("Temperature: " + customParam.getDoubleParam() + "℃");
                    updateDial(customParam.getDoubleParam());
                }
            }
        });
    }

    @Override
    public void setCaseListener(CaseListener listener) {
        this.caseListener = listener;
    }

    @Override
    public void setValueListener(ValueListener listener) {
        this.valueListener = listener;
    }

    private void printLog(String message) {
        if (caseListener != null)
            caseListener.onLog(message);
    }

    private void updateDial(double temperature) {
        if (valueListener != null)
            valueListener.onUpdateDial(temperature);
    }
}
