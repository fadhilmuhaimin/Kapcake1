package com.exomatik.kapcake.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.kapcake.Authentication.ActAuthPin;
import com.exomatik.kapcake.Authentication.ActSignIn;
import com.exomatik.kapcake.Featured.Bluetooth;
import com.exomatik.kapcake.Featured.CustomWebChromeClient;
import com.exomatik.kapcake.Featured.Print;
import com.exomatik.kapcake.Featured.WebViewJavaScriptInterface;
import com.exomatik.kapcake.Featured.UserSave;
import com.exomatik.kapcake.R;
import com.google.android.material.snackbar.Snackbar;


import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    public static WebView web;
    public static int toAuth = 0;
    public static boolean jalan = true;
    private UserSave userSave;
    public ProgressDialog progressDialog;
    private boolean back = true;
    private boolean tampil = true;
    private boolean statusBluetooth = false;
    private Bluetooth bluetoothClass;
    private Print printClass;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideStatusBar();
        setContentView(R.layout.activity_main);

        init();
        runnabelCekKoneksi();
//        web.clearCache(true);
//        web.clearHistory();
//
//        web.setWebChromeClient(new CustomWebChromeClient(this));
//        web.setWebViewClient(new WebViewClient());
//        web.getSettings().setLoadsImagesAutomatically(true);
//        web.getSettings().setJavaScriptEnabled(true);
//        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        web.getSettings().setDomStorageEnabled(true);
//
//        web.getSettings().setSupportZoom(false);
//        web.getSettings().setBuiltInZoomControls(false);
//        web.getSettings().setDisplayZoomControls(false);
//
//        web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//
//        web.loadUrl("file:///android_asset/index.html");
//
//        web.setWebViewClient(new WebViewClient() {
//            public void onPageFinished(WebView view, String weburl) {
//                if (jalan) {
//                    web.loadUrl("javascript:loginUser(" + new Gson().toJson(userSave.getKEY_USER()) + ")");
//                    jalan = false;
//                }
//            }
//        });
//        WebViewJavaScriptInterface webViewJS = new WebViewJavaScriptInterface(this, MainActivity.this
//                , progressDialog, bluetoothClass, printClass, statusBluetooth, view);
//        web.addJavascriptInterface(webViewJS, "android");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (toAuth == 1){
            Intent intent = new Intent(getApplicationContext(), ActAuthPin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            toAuth = 0;
        }
        else if (toAuth == 2){
            finish();
            userSave.setKEY_USER(null);
            Intent intent = new Intent(getApplicationContext(), ActSignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        else {
            WebViewJavaScriptInterface webViewJS = new WebViewJavaScriptInterface(this, MainActivity.this
                    , progressDialog, bluetoothClass, printClass, statusBluetooth, view);
            web.addJavascriptInterface(webViewJS, "android");
        }
    }

    private void runnabelCekKoneksi() {
        new CountDownTimer(300000, 1000) {
            public void onTick(long millisUntilFinished) {
                if (back) {
                    cekKoneksi();
                }
            }

            public void onFinish() {
            }

        }.start();
    }

    private void customSnackbar(String text, int background) {
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);

        // Get the Snackbar view
        View view = snackbar.getView();

        view.setBackground(ContextCompat.getDrawable(this, background));
        TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);

        tv.setTextColor(Color.parseColor("#FFFFFF"));

        snackbar.setText(text);
        snackbar.show();
    }

    private void cekKoneksi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                tampil = true;
            } else {
                if (tampil) {
                    customSnackbar("Mohon periksa koneksi internet anda", R.drawable.snakbar_red);
                    tampil = false;
                }
            }
        }
    }

    private void init() {
        web = (WebView) findViewById(R.id.web);
        view  = (View) findViewById(android.R.id.content);
        userSave = new UserSave(this);
        bluetoothClass = new Bluetooth(this, web);

        overridePendingTransition(0, 0);

    }

    @Override
    public void onBackPressed() {
        back = false;
        finish();
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth aktif", Toast.LENGTH_SHORT).show();

                    bluetoothClass.btnDiscover(1);
                } else {
                    Toast.makeText(this, "Bluetooth tidak aktif", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (statusBluetooth){
            // Don't forget to unregister the ACTION_FOUND receiver.
            unregisterReceiver(bluetoothClass.receiverDevice);
            try {
                printClass.closeBT();
            } catch (IOException e) {
                e.printStackTrace();
            }
            statusBluetooth = false;
        }
    }

}
