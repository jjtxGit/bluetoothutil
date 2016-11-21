package com.example.bluetoothutil;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothutil.util.BluetoothHelperListener;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by jjtx on 2016/11/20.
 */

public class MyBluetoothListener implements BluetoothHelperListener {
    public static final int RECEIVED_MESSAGE = 1;

    private TextView receivedMsgTv;
    private Context context;
    private ListView lv;


    private static MyBluetoothListener myBluetoothListener = null;

    public static MyBluetoothListener getMyBluetoothListener() {
        if (myBluetoothListener == null) {
            myBluetoothListener = new MyBluetoothListener();
        }
        return myBluetoothListener;
    }


    private MyBluetoothListener() {
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public void setLv(ListView lv) {
        this.lv = lv;
    }

    public void setReceivedMsgTv(TextView receivedMsgTv) {
        this.receivedMsgTv = receivedMsgTv;
    }

    @Override
    public void onFoundDevices(List<BluetoothDevice> devices) {
        showToast("发现一个设备");
        lv.setAdapter(new MyAdapter(devices, context));
    }

    @Override
    public void onGetSocketConnect() {
        Intent intent = new Intent(context, ChatWithBluetoothActivity.class);
        context.startActivity(intent);
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
        String msg = null;
        try {
            msg = new String(bytes, 0, length, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("收到消息" + msg);

        Message message = new Message();
        message.what = RECEIVED_MESSAGE;
        message.obj = msg;

        handler.sendMessage(message);

    }


    @Override
    public void onEndReceivedMsg() {

    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    private class MyAdapter extends BaseAdapter {

        private Context context;
        private List<BluetoothDevice> devices;

        public MyAdapter(List<BluetoothDevice> devices, Context context) {
            this.devices = devices;
            this.context = context;
        }


        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.lv_item, null);
            final TextView bluetoothInfor = (TextView) view.findViewById(R.id.bluetoothInfor);
            Button connectBt = (Button) view.findViewById(R.id.connectBt);

            bluetoothInfor.setText(devices.get(position).getName());

            connectBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BluetoothDevice device = devices.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("deviceMac", device.getAddress());
                    Intent intent = new Intent(context, ChatWithBluetoothActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

            return view;
        }

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case RECEIVED_MESSAGE:
                    receivedMsgTv.setText((String) msg.obj);
                    break;
            }


        }
    };


}
