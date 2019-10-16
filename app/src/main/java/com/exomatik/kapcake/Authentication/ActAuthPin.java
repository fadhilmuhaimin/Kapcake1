package com.exomatik.kapcake.Authentication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.exomatik.kapcake.Featured.CustomWebChromeClient;
import com.exomatik.kapcake.Featured.UserSave;
import com.exomatik.kapcake.Activity.MainActivity;
import com.exomatik.kapcake.Featured.WebViewJavaScriptInterface;
import com.exomatik.kapcake.R;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;

public class ActAuthPin extends AppCompatActivity {
    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnClear;
    private ProgressDialog progressDialog;
    private ImageView img1, img2, img3, img4;
    private String pin;
    private UserSave userSave;
    private ImageButton btnLogout;
    private int coba = 0;
    private boolean jalan = true;
    private int timeCountDown = 30;
    private View view;
    private TextView textUser;
    private CountDownTimer time;
    private boolean timeRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideStatusBar();
        setContentView(R.layout.act_auth_pin);

        init();

        onClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timeRun){
            time.cancel();
        }
    }

    private void init() {
        textUser = (TextView) findViewById(R.id.text_hint);
        btn1 = (Button) findViewById(R.id.btn_1);
        btn2 = (Button) findViewById(R.id.btn_2);
        btn3 = (Button) findViewById(R.id.btn_3);
        btn4 = (Button) findViewById(R.id.btn_4);
        btn5 = (Button) findViewById(R.id.btn_5);
        btn6 = (Button) findViewById(R.id.btn_6);
        btn7 = (Button) findViewById(R.id.btn_7);
        btn8 = (Button) findViewById(R.id.btn_8);
        btn9 = (Button) findViewById(R.id.btn_9);
        btn0 = (Button) findViewById(R.id.btn_0);
        btnClear = (Button) findViewById(R.id.btn_clear);
        img1 = (ImageView) findViewById(R.id.img_1);
        img2 = (ImageView) findViewById(R.id.img_2);
        img3 = (ImageView) findViewById(R.id.img_3);
        img4 = (ImageView) findViewById(R.id.img_4);
        btnLogout = (ImageButton) findViewById(R.id.btn_logout);

        view = (View) findViewById(android.R.id.content);

        userSave = new UserSave(this);

        textUser.setText(userSave.getKEY_USER().getUser().getNama());

        img1.setImageResource(R.drawable.border_hitam_putih);
        img2.setImageResource(R.drawable.border_hitam_putih);
        img3.setImageResource(R.drawable.border_hitam_putih);
        img4.setImageResource(R.drawable.border_hitam_putih);

        overridePendingTransition(0, 0);
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void onClick() {
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pin = null;
                img1.setImageResource(R.drawable.border_hitam_putih);
                img2.setImageResource(R.drawable.border_hitam_putih);
                img3.setImageResource(R.drawable.border_hitam_putih);
                img4.setImageResource(R.drawable.border_hitam_putih);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               alertLogout();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("1");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("2");
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("3");
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("4");
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("5");
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("6");
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("7");
            }
        });
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("8");
            }
        });
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("9");
            }
        });
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText("0");
            }
        });
    }

    private void alertLogout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ActAuthPin.this);

        alert.setTitle("Keluar");
        alert.setMessage("Apakah anda yakin ingin keluar dari akun?");
        alert.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                customSnackbar("Berhasil Logout", R.drawable.snakbar_blue);
                finish();
                MainActivity.toAuth = 2;
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

    private void setText(String value){
        if (coba < 3){
            if (pin == null){
                pin = value;
                img1.setImageResource(R.drawable.border_hitam);
                img2.setImageResource(R.drawable.border_hitam_putih);
                img3.setImageResource(R.drawable.border_hitam_putih);
                img4.setImageResource(R.drawable.border_hitam_putih);
            }
            else if (pin.length() == 1){
                pin = pin + value;
                img1.setImageResource(R.drawable.border_hitam);
                img2.setImageResource(R.drawable.border_hitam);
                img3.setImageResource(R.drawable.border_hitam_putih);
                img4.setImageResource(R.drawable.border_hitam_putih);
            }
            else if (pin.length() == 2){
                pin = pin + value;
                img1.setImageResource(R.drawable.border_hitam);
                img2.setImageResource(R.drawable.border_hitam);
                img3.setImageResource(R.drawable.border_hitam);
                img4.setImageResource(R.drawable.border_hitam_putih);
            }
            else if (pin.length() == 3){
                pin = pin + value;
                img1.setImageResource(R.drawable.border_hitam);
                img2.setImageResource(R.drawable.border_hitam);
                img3.setImageResource(R.drawable.border_hitam);
                img4.setImageResource(R.drawable.border_hitam);

                if (pin.equals(Integer.toString(userSave.getKEY_USER().getUser().getPin()))){
                    setWebView();
                }else {
                    coba++;
                    img1.setImageResource(R.drawable.border_hitam_putih);
                    img2.setImageResource(R.drawable.border_hitam_putih);
                    img3.setImageResource(R.drawable.border_hitam_putih);
                    img4.setImageResource(R.drawable.border_hitam_putih);
                    pin = null;

                    customSnackbar(getResources().getString(R.string.error_pin_salah), R.drawable.snakbar_red);
                    if (coba == 3){
                        timer();
                        timeRun = true;
                    }
                }
            }
        }
        else {
            customSnackbar(getResources().getString(R.string.time_count_down)
                    + " " + Integer.toString(timeCountDown)
                    + " " + getResources().getString(R.string.time_count_down2), R.drawable.snakbar_red);
        }

    }

    public void progressShow(String message) {
        progressDialog = new ProgressDialog(ActAuthPin.this, R.style.MyProgressDialogTheme);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    private void setWebView() {
        progressShow("Mohon tunggu");
        MainActivity.web.clearCache(true);
        MainActivity.web.clearHistory();

        MainActivity.web.setWebChromeClient(new CustomWebChromeClient(this));
        MainActivity.web.setWebViewClient(new WebViewClient());
        MainActivity.web.getSettings().setLoadsImagesAutomatically(true);
        MainActivity.web.getSettings().setJavaScriptEnabled(true);
        MainActivity.web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        MainActivity.web.getSettings().setDomStorageEnabled(true);

        MainActivity.web.getSettings().setSupportZoom(false);
        MainActivity.web.getSettings().setBuiltInZoomControls(false);
        MainActivity.web.getSettings().setDisplayZoomControls(false);

        MainActivity.web.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        MainActivity.web.loadUrl("file:///android_asset/index.html");

        MainActivity.web.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String weburl) {
                pin = null;
                MainActivity.toAuth = 0;

                if (MainActivity.jalan) {
                    MainActivity.web.loadUrl("javascript:loginUser(" + new Gson().toJson(userSave.getKEY_USER()) + ")");
                    MainActivity.jalan = false;
                }

                new Handler().postDelayed(new Runnable()
                {
                    public void run()
                    {
                        progressDialog.dismiss();
                        finish();
                    }
                }, 2000L);
            }
        });
    }

    private void timer(){
        time = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeCountDown--;
                Log.e("Timer",Integer.toString(timeCountDown));

                if (timeCountDown == 0){
                    coba = 0;
                    timeCountDown = 30;
                    timeRun = false;
                    cancel();
                }
            }

            public void onFinish() {
                Log.e("Timer",Integer.toString(timeCountDown));
            }

        }.start();
    }
}
