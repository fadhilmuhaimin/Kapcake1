package com.exomatik.kapcake.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.exomatik.kapcake.Activity.MainActivity;
import com.exomatik.kapcake.Adapter.SwipeAdapter;
import com.exomatik.kapcake.Featured.UserSave;
import com.exomatik.kapcake.Model.ModelUser;
import com.exomatik.kapcake.R;
import com.exomatik.kapcake.Retrofit.PostCekLogin;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.santalu.autoviewpager.AutoViewPager;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActSignIn extends AppCompatActivity {
    private Button btnLogin, btnSignUp;
    private ProgressDialog progressDialog;
    private TextInputLayout etInputEmail, etInputPass;
    private View view;
    private UserSave userSave;
    private WormDotsIndicator dotsIndicator;
    private AutoViewPager viewPager;
    private SwipeAdapter swipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideStatusBar();
        setContentView(R.layout.act_sign_in_potrait1);
        init();
//        setAdapter();

//        onClick();
    }

    private void init() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        etInputEmail = (TextInputLayout) findViewById(R.id.et_input_email);
//        etInputPass = (TextInputLayout) findViewById(R.id.et_input_pass);
//        view = (View) findViewById(android.R.id.content);
//        viewPager = (AutoViewPager) findViewById(R.id.viewPager);
//        dotsIndicator = (WormDotsIndicator) findViewById(R.id.dotsIndicator);
//
//        userSave = new UserSave(this);
//        overridePendingTransition(0, 0);
    }

    private void setAdapter() {
        String title[] = {"HALO IRFAN",
                "HALO RICKY",
                "HALO ANDI"};
        String isi[] = {"Apa yang sedang anda pikirkan?",
                "Apa yang sedang anda lakukan",
                "Adaji dibikin kak?"};
        swipeAdapter = new SwipeAdapter(this, title, isi);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(swipeAdapter);
        dotsIndicator.setViewPager(viewPager);
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void onClick() {
        etInputEmail.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                etInputEmail.getEditText().setFocusable(false);
                etInputPass.getEditText().setFocusable(true);
                return false;
            }
        });

//        etInputPass.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                etInputEmail.getEditText().setFocusable(false);
//                etInputPass.getEditText().setFocusable(false);
//                etInputEmail.getEditText().setFocusableInTouchMode(true);
//                etInputPass.getEditText().setFocusableInTouchMode(true);
//                return false;
//            }
//        });

//        btnSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = "https://backoffice.kapcake.com/register";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//        });

//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = etInputEmail.getEditText().getText().toString();
////                String pass = etInputPass.getEditText().getText().toString();
//
//                if (email.isEmpty() ||  pass.isEmpty() || pass.length() < 6){
//                    if (email.isEmpty()){
//                        customSnackbar("Email " + getResources().getString(R.string.error_data_kosong), R.drawable.snakbar_red);
//                    }
//                    else if (pass.isEmpty()){
//                        customSnackbar("Password " + getResources().getString(R.string.error_data_kosong), R.drawable.snakbar_red);
//                    }
//                    else if (pass.length() < 6){
//                        customSnackbar("Password " + getResources().getString(R.string.error_password_kurang), R.drawable.snakbar_red);
//                    }
//                }
//                else {
//                    progressDialog = new ProgressDialog(ActSignIn.this);
//                    progressDialog.setMessage(getResources().getString(R.string.progress_title1));
//                    progressDialog.setCancelable(false);
//                    progressDialog.show();
//                    postLoginUser(email, pass);
//                }
//            }
//        });

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });

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

    private void postLoginUser(String email, String pass){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PostCekLogin.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PostCekLogin apiSearchMovie = retrofit.create(PostCekLogin.class);

        Call<ModelUser> call = apiSearchMovie.searchMovie("kasir/login?email=" + email +"&password=" + pass);

        call.enqueue(new Callback<ModelUser>() {
            @Override
            public void onResponse(Call<ModelUser> call, Response<ModelUser> response) {
                ModelUser films = response.body();

                if (films == null){
                    customSnackbar(getResources().getString(R.string.error_email_not_found), R.drawable.snakbar_red);
                }
                else {
                    userSave.setKEY_USER(films);
                    MainActivity.toAuth = 1;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ModelUser> call, Throwable t) {
                progressDialog.dismiss();
                if (t.getMessage().toString().contains("Unable to resolve host")){
                    customSnackbar("Mohon periksa koneksi Internet Anda", R.drawable.snakbar_red);
                }
                else {
                    customSnackbar(t.getMessage().toString(), R.drawable.snakbar_red);
                }
            }
        });
    }
}
