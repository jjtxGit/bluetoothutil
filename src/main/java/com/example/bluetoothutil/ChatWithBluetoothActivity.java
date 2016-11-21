package com.example.bluetoothutil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothutil.util.BluetoothHelper;
import com.example.bluetoothutil.util.BluetoothReceiver;

import java.io.UnsupportedEncodingException;

/**
 * Created by jjtx on 2016/11/20.
 */

public class ChatWithBluetoothActivity extends Activity {


    private MyBluetoothListener myBluetoothListener = MyBluetoothListener.getMyBluetoothListener();

    private BluetoothHelper bluetoothHelper = BluetoothHelper.getBluetoothHelper();

    private TextView sendMsgTv;
    private TextView receivedTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_main);
        String deviceMac = getIntent().getStringExtra("deviceMac");

        if (deviceMac != null) {
            bluetoothHelper.startConnect(deviceMac);
        }

        initUI();

    }


    public void send(View view) {
        String msg = sendMsgTv.getText().toString().trim();
        byte[] bytes = null;
        try {
            bytes = msg.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (bluetoothHelper.isReadySend()) {
            bluetoothHelper.send(bytes, 0, bytes.length);
            showToast("发送成功");
        }

    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void initUI() {
        sendMsgTv = (TextView) findViewById(R.id.sendMsg);
        receivedTv = (TextView) findViewById(R.id.receivedMsg);
        myBluetoothListener.setReceivedMsgTv(receivedTv);
    }

}
