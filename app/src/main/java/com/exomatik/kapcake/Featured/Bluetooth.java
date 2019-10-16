package com.exomatik.kapcake.Featured;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.exomatik.kapcake.Activity.GetKoneksi;
import com.exomatik.kapcake.Activity.MainActivity;
import com.exomatik.kapcake.Model.ModelBluetooth;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by IrfanRZ on 18/08/2019.
 */

public class Bluetooth {
    private BluetoothAdapter bluetoothAdapter;
    private Activity activity;
    private int request;
    private WebView web;
    private ArrayList<ModelBluetooth> listDevice = new ArrayList<ModelBluetooth>();

    public Bluetooth(Activity activity, WebView web) {
        this.activity = activity;
        this.web = web;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void btnDiscover(int request) {
        this.request = request;
        Log.e("Looking", "For Device");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.e("btn", "Cancel Discover");

            cekPermission();

            bluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            activity.registerReceiver(receiverDevice, filter);
        }

        if (!bluetoothAdapter.isDiscovering()) {
            cekPermission();
            Log.e("btn", "Discover");

            bluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            activity.registerReceiver(receiverDevice, filter);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void cekPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = activity.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += activity.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");

            if (permissionCheck != 0) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        } else {
            Log.e("Tag", "No Need permission < Lollipop");
        }
    }

    public BroadcastReceiver receiverDevice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (listDevice.size() != 0) {
                    boolean tidakAda = true;

                    for (int a = 0; a < listDevice.size(); a++) {
                        if (listDevice.get(a).getMac().equals(device.getAddress())) {
                            tidakAda = false;
                        }
                    }

                    if (tidakAda) {
                        listDevice.add(new ModelBluetooth(device.getName(), device.getAddress()));
                    }
                } else {
                    listDevice.add(new ModelBluetooth(device.getName(), device.getAddress()));
                }

                web.loadUrl("javascript:setListPrinterDitemukan(" + new Gson().toJson(listDevice) + ")");
            }
        }
    };
}
