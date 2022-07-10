package com.verisilicon.sample.cases.protocol;

import androidx.annotation.NonNull;

import com.verisilicon.protocol.vs.ProtocolBase;
import com.verisilicon.protocol.vs.requests.AbstractRequest;
import com.verisilicon.protocol.vs.requests.OpCode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CustomParam extends AbstractRequest {
    public static final byte TEMPERATURE_TYPE = 0x0d;

    public static final short CUSTOM_PARAM_EVENT_CODE = OpCode.OP_CUSTOM_PARAM_START + 1;
    private static final int PARAM_LENGTH = 5;
    private byte type;
    private int param;

    public CustomParam(byte type, int param) {
        super(new byte[ProtocolBase.HEADER_LENGTH + PARAM_LENGTH + ProtocolBase.CRC_LENGTH]);
    }

    public CustomParam(ProtocolBase protocolBase) {
        super(protocolBase);
    }

    @Override
    public short opCode() {
        return CUSTOM_PARAM_EVENT_CODE;
    }

    @Override
    public byte[] params() {
        byte[] params = new byte[PARAM_LENGTH];
        ByteBuffer byteBuffer = ByteBuffer.wrap(params);

        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(type);
        byteBuffer.putInt(param);
        return byteBuffer.array();
    }

    @Override
    public void unpack() {
        super.unpack();
        this.type = byteBuffer.get();
        this.param = byteBuffer.getInt();
    }

    @NonNull
    @Override
    public String toString() {
        return "CustomParam{" +
                "type=" + type +
                ", param=" + param +
                '}';
    }

    public byte getType() {
        return type;
    }

    public int getParam() {
        return param;
    }

    public double getDoubleParam() {
        return (double)param / 1000;
    }
}
