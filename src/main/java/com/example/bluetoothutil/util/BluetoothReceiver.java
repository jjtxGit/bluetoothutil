package com.example.bluetoothutil.util;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjtx on 2016/11/18.
 */

public class BluetoothReceiver extends BroadcastReceiver {


    private List<BluetoothDevice> devices;
    private BluetoothHelperListener listener;

    private static BluetoothReceiver receiver = null;

    public static BluetoothReceiver getReceiver() {
        return receiver;
    }

    public static BluetoothReceiver getReceiver(BluetoothHelperListener listener) {
        if (receiver == null) {
            receiver = new BluetoothReceiver(listener);
        }

        return receiver;
    }

    private BluetoothReceiver(BluetoothHelperListener listener) {

        if (this.devices == null) {
            this.devices = new ArrayList<>();
        }

        this.listener = listener;

    }

    /**
     * 发现新的蓝牙设备
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        switch (action) {
            case BluetoothDevice.ACTION_FOUND:

                if (devices != null && !devices.contains(bluetoothDevice)) {
                    devices.add(bluetoothDevice);
                    listener.onFoundDevices(devices);
                }
                break;
        }
    }

}
