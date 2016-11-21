package com.example.bluetoothutil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bluetoothutil.util.BluetoothHelper;

public class MainActivity extends Activity {

    private BluetoothHelper bluetoothHelper;

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv);

        MyBluetoothListener myBluetoothListener = MyBluetoothListener.getMyBluetoothListener();
        myBluetoothListener.setContext(this);
        myBluetoothListener.setLv(lv);

        this.bluetoothHelper = BluetoothHelper.getBluetoothHelper(myBluetoothListener, this, null);

    }

    public void scan(View view) {

        if (!bluetoothHelper.isBluetoothEnable()) {
            showToast("蓝牙未开启");
            bluetoothHelper.bluetoothEnable();
            return;
        }


        if (bluetoothHelper.isDiscovering()) {
            showToast("正在搜寻中");
            return;
        }

        bluetoothHelper.startDiscover();

        showToast("开始搜寻");

    }

    public void setVisible(View view) {

        bluetoothHelper.setBluetoothVisible(this);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
