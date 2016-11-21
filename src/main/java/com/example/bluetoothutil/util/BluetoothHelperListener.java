package com.example.bluetoothutil.util;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Created by jjtx on 2016/11/20.
 */

public interface BluetoothHelperListener {

    void onFoundDevices(List<BluetoothDevice> devices);

    void onGetSocketConnect();

    void onGetDeviceConnect(BluetoothDevice device);

    void onDisConnect();


    /**
     * <strong>该方法将在子线程执行</strong>。若想修改ui线程内容，请使用android.os.Handler类
     */
    void onBeginReceivedMsg();

    /**
     * <strong>该方法将在子线程执行</strong>。若想修改ui线程内容，请使用android.os.Handler类
     *
     * @param bytes
     * @param length
     */
    void onReceivedMsg(byte[] bytes, int length);

    /**
     * <strong>该方法将在子线程执行</strong>。若想修改ui线程内容，请使用android.os.Handler类
     */
    void onEndReceivedMsg();




}
