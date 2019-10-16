package com.exomatik.kapcake.Featured;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.kapcake.Authentication.ActSignIn;
import com.exomatik.kapcake.Model.ModelPesanan;
import com.exomatik.kapcake.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.IOException;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

/**
 * Created by IrfanRZ on 03/08/2019.
 */

public class WebViewJavaScriptInterface {
    private Context context;
    private Activity activity;
    private UserSave userSave;
    private ProgressDialog progressDialog;
    private Bluetooth bluetoothClass;
    private BluetoothAdapter bluetoothAdapter;
    private Print printClass;
    private Boolean statusBluetooth;
    private View view;
    /*
     * Need a reference to the context in order to sent a post message
     */

    public WebViewJavaScriptInterface(Context context, Activity activity, ProgressDialog progressDialog
            , Bluetooth bluetoothClass, Print printClass, Boolean statusBluetooth, View view) {
        this.view = view;
        this.context = context;
        this.activity = activity;
        this.progressDialog = progressDialog;
        this.bluetoothClass = bluetoothClass;
        this.printClass = printClass;
        this.statusBluetooth = statusBluetooth;

        userSave = new UserSave(context);
    }

    private void customSnackbar(String text, int background) {
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);

        // Get the Snackbar view
        View view = snackbar.getView();

        view.setBackground(ContextCompat.getDrawable(activity, background));
        TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);

        tv.setTextColor(Color.parseColor("#FFFFFF"));

        snackbar.setText(text);
        snackbar.show();
    }

    /*
     * This method can be called from Android. @JavascriptInterface
     * required after SDK version 17.
     */
    @JavascriptInterface
    public void logout() {
        alertLogout();
    }

    private void alertLogout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        alert.setTitle("Keluar");
        alert.setMessage("Apakah anda yakin ingin keluar dari akun?");
        alert.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                userSave.setKEY_USER(null);
                activity.startActivity(new Intent(context, ActSignIn.class));
                activity.finish();
                customSnackbar(context.getResources().getString(R.string.text_berhasil_logout), R.drawable.snakbar_blue);
            }
        });
        alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    @JavascriptInterface
    public void progressShow(String title, String message) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(title);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @JavascriptInterface
    public void progressDismiss() {
        progressDialog.dismiss();
    }

    @JavascriptInterface
    public void ToastMessage(String message) {
        customSnackbar(message, R.drawable.snakbar_blue);
    }

    @JavascriptInterface
    public void printPesanan(String message) {
        ModelPesanan obj = new Gson().fromJson(message, ModelPesanan.class);

        printClass.printData(obj);
    }

    @JavascriptInterface
    public void cekAndRequestBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth tidak didukung", Toast.LENGTH_SHORT).show();
        }

        if (bluetoothAdapter.isEnabled()) {
            Toast.makeText(context, "Bluetooh Aktif", Toast.LENGTH_SHORT).show();

            bluetoothClass.btnDiscover(2);
        }
        else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, 0);
            Toast.makeText(context, "Menyalakan", Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void connectingToBluetooth(String macAddress) {
        printClass = new Print(bluetoothAdapter.getRemoteDevice(macAddress), activity, bluetoothAdapter);
        statusBluetooth = true;

        printClass.hubungkanDevice();
    }

    @JavascriptInterface
    public void tesPrint(String namaOutlet, String hp, String alamat, String tipe, String macAddress) {
        Log.e("Tes", "Succes print");

        if (namaOutlet.isEmpty()){
            namaOutlet = "Kapcake";
        }
        if (hp.isEmpty()){
            hp = "-";
        }
        if (alamat.isEmpty()){
            alamat = "Villa Samata";
        }
        if (tipe.isEmpty()){
            tipe = "Bluetooth";
        }
        if (macAddress.isEmpty()){
            macAddress = "-";
        }
        printClass.tesPrint(namaOutlet, hp, alamat, tipe, macAddress);
    }

    @JavascriptInterface
    public void stopConnected() {
        statusBluetooth = false;
        activity.unregisterReceiver(bluetoothClass.receiverDevice);
        try {
            printClass.closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
