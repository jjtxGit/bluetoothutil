package com.example.bluetoothutil.util;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Created by jjtx on 2016/11/20.
 */

public class DefaultBluetoothHelperListener implements BluetoothHelperListener {

    @Override
    public void onFoundDevices(List<BluetoothDevice> devices) {

    }

    @Override
    public void onGetSocketConnect() {

    }

    @Override
    public void onGetDeviceConnect(BluetoothDevice device) {

    }

    @Override
    public void onDisConnect() {

    }

    @Override
    public void onBeginReceivedMsg() {

    }

    @Override
    public void onReceivedMsg(byte[] bytes, int length) {

    }

    @Override
    public void onEndReceivedMsg() {

    }
}
