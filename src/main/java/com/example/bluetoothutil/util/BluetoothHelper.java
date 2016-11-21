package com.example.bluetoothutil.util;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by jjtx on 2016/11/20.
 */

public class BluetoothHelper {

    private static final UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static BluetoothHelper bluetoothHelper = null;

    public static BluetoothHelper getBluetoothHelper() {
        return bluetoothHelper;
    }

    /**
     * 获取蓝牙帮助类的方法
     *
     * @param listener
     * @param context
     * @param uuid：通信协议，如果为空，则默认为 UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
     * @return
     */
    public static BluetoothHelper getBluetoothHelper(BluetoothHelperListener listener, Context context, UUID uuid) {
        if (bluetoothHelper == null) {
            bluetoothHelper = new BluetoothHelper(listener, context, uuid);
        }
        return bluetoothHelper;
    }

    //蓝牙适配器
    private BluetoothAdapter adapter;

    //蓝牙帮助类监听器
    private BluetoothHelperListener listener;

    //蓝牙Socket
    private BluetoothSocket socket;

    //蓝牙服务uuid
    private UUID uuid;

    //socket服务器，用来监听发来的信息
    private Thread serverListenerThread;

    private BluetoothHelper(BluetoothHelperListener listener, Context context, UUID uuid) {

        if (listener == null) {
            listener = new DefaultBluetoothHelperListener();
        }

        if (uuid == null) {
            uuid = DEFAULT_UUID;
        }

        this.listener = listener;
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        this.uuid = uuid;

        initBroadCast(context);

        //开始监听是否有连接，若有连接，则作为服务器
        serverListenerThread = new ServerListenerThread(uuid);
        serverListenerThread.start();

    }

    /**
     * 检测本机是否存在蓝牙适配器
     *
     * @return
     */

    public boolean hasBluetoothAdapter() {
        return adapter != null;
    }


    /**
     * 设置蓝牙可见
     */
    public void setBluetoothVisible(Context context) {

        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        context.startActivity(intent);
    }

    /**
     * 蓝牙是否可用
     */
    public boolean isBluetoothEnable() {
        return adapter.isEnabled();
    }


    public void bluetoothEnable() {
        adapter.enable();
    }


    public boolean isDiscovering() {
        return adapter.isDiscovering();
    }

    /**
     * 开始扫描
     * 利用广播扫描蓝牙，每当发现一个蓝牙设备，就往list里动态添加设备
     */
    public void startDiscover() {
        adapter.startDiscovery();
    }


    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }


    /**
     * @param bluetoothAddress：蓝牙的mac地址
     */
    public void startConnect(String bluetoothAddress) {

        Thread connectThread = new ConnectThread(bluetoothAddress, uuid);
        connectThread.start();
    }

    public void closeConnect() {

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isReadySend() {

        try {
            return isConnected() && socket.getOutputStream() != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void send(byte[] bytes, int offset, int length) {
        Thread sendThread = new SendThread(bytes, offset, length);
        sendThread.start();
    }


    /**
     * 客户端连接线程
     */
    private class ConnectThread extends Thread {

        private String mac;
        private UUID uuid;
        private BluetoothDevice remoteDevice;

        public ConnectThread(String mac, UUID uuid) {
            this.mac = mac;
            this.uuid = uuid;
        }

        /**
         * 线程运行的方法
         */
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {

            //如果连接已经成功，就不在再连接
            if (isConnected()) {
                return;
            }

            //关闭连接监听
            ((ServerListenerThread) serverListenerThread).runFlage = false;

            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            //取消设备发现
            adapter.cancelDiscovery();
            remoteDevice = adapter.getRemoteDevice(mac);

            //如果没有配对
            if (remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                remoteDevice.createBond();
            }

            //开始连接，作为客户端
            try {

                socket = remoteDevice.createInsecureRfcommSocketToServiceRecord(uuid);
                if (!socket.isConnected())
                    socket.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //开启信息监听
            Thread receivedThread = new ReceivedThread();
            receivedThread.start();

        }

    }


    /**
     * 服务器监听线程
     */
    private class ServerListenerThread extends Thread {
        private UUID uuid;
        public boolean runFlage = true;

        public ServerListenerThread(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public void run() {

            BluetoothServerSocket serverSocket = null;

            while (serverSocket == null) {
                try {
                    serverSocket = adapter.listenUsingRfcommWithServiceRecord("blueService", uuid);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while (runFlage) {
                try {
                    //调用监听器的 onGetSocketConnect 方法
                    socket = serverSocket.accept();
                    //开启信息监听
                    Thread receivedThread = new ReceivedThread();
                    receivedThread.start();

                    listener.onGetSocketConnect();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 数据发送线程
     */
    private class SendThread extends Thread {

        private byte[] msg;
        private int offset;
        private int count;

        public SendThread(byte[] msg, int offset, int count) {
            this.msg = msg;
            this.offset = offset;
            this.count = count;
        }

        @Override
        public void run() {
            try {
                OutputStream os = socket.getOutputStream();
                os.write(msg, offset, count);
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 数据接收线程
     */
    private class ReceivedThread extends Thread {

        @Override
        public void run() {
            InputStream is = null;

            try {
                is = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] bytes = new byte[1024];
            int length = 0;

            while (true) {
                try {

                    //调用监听器 开始接收数据
                    listener.onBeginReceivedMsg();

                    while ((length = is.read(bytes)) != -1) {
                        //处理数据
                        listener.onReceivedMsg(bytes, length);
                    }

                    //数据接收完毕
                    listener.onEndReceivedMsg();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }


    /**
     * 注册广播监听器，当发现新设备的时候，调用BluetoothHelperListener的onFoundDevice方法
     *
     * @param context
     */
    private void initBroadCast(Context context) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        BluetoothReceiver bluetoothReceiver = BluetoothReceiver.getReceiver(listener);

        context.registerReceiver(bluetoothReceiver, intentFilter);
    }


}
