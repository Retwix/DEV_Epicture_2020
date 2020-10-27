package com.epicture.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.epicture.Epicture_global;
import com.epicture.R;

public class signin extends AppCompatActivity {

    Button Signin;
    TextView Noconnection;
    TextView register;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Signin = findViewById(R.id.buttonSignIn);
        Epicture_global.myWebView = findViewById(R.id.imguroauth);
        Epicture_global.myWebView.setWebViewClient(new MyWebViewClient(this));
        Epicture_global.myWebView.setVisibility(View.GONE);
        Epicture_global.myWebView.loadUrl("https://api.imgur.com/oauth2/authorize?client_id=0be29f86c5c606f&response_type=token");

        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Epicture_global.access_token == null) {
                    Epicture_global.myWebView.setVisibility(View.VISIBLE);
                }
            }
        });

        Noconnection = (TextView) findViewById(R.id.Noco);
        Noconnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(signin.this, dashboard.class);
                startActivity(i);
            }
        });

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://imgur.com/register?redirect=https%3A%2F%2Fimgur.com%2F"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && Epicture_global.myWebView.canGoBack()) {
            Epicture_global.myWebView.goBack();
            return true;
        } else {
            Epicture_global.myWebView.setVisibility(View.GONE);
            Epicture_global.myWebView.loadUrl("https://api.imgur.com/oauth2/authorize?client_id=0be29f86c5c606f&response_type=token");
        }
        return true;
    }

    class MyWebViewClient extends WebViewClient {
        String[] splitedurl = null;
        Context ctx;

        public void createDialog() {
            final AlertDialog.Builder alert = new AlertDialog.Builder(signin.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_loading, null);
            alert.setView(mView);
            final AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        public boolean isInternetConnection() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        }

        public MyWebViewClient(Context context) {

            mPrefs = getSharedPreferences("access_token", 0);
            mEditor = mPrefs.edit();
            if (mPrefs.getString("access_token", null) != null) {
                if (isInternetConnection()) {
                    Epicture_global.username = mPrefs.getString("username", null);
                    Epicture_global.access_token = mPrefs.getString("access_token", null);
                    if (Epicture_global.access_token != null) {
                        Intent dashboard = new Intent(signin.this, com.epicture.View.dashboard.class);
                        context.startActivity(dashboard);
                    }
                } else {
                    createDialog();
                }
            }
            this.ctx = context;
        }

        public void GalleriesRequests(final Context context) {
            if (Epicture_global.access_token != null) {
                Intent dashboard = new Intent(signin.this, com.epicture.View.dashboard.class);
                context.startActivity(dashboard);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.contains("access_token")) {
                url = url.replace('#', '&');
                splitedurl = url.split("&");
                Epicture_global.access_token = (splitedurl[1].split("="))[1];
                mEditor.putString("access_token", Epicture_global.access_token).commit();
                Epicture_global.expires_in = (splitedurl[2].split("="))[1];
                Epicture_global.token_type = (splitedurl[3].split("="))[1];
                Epicture_global.refresh_token = (splitedurl[4].split("="))[1];
                Epicture_global.username = (splitedurl[5].split("="))[1];
                mEditor.putString("username", Epicture_global.username).commit();
                Epicture_global.account_id = (splitedurl[6].split("="))[1];
                //Epicture_global.myWebView.setVisibility(View.GONE);
                if (Epicture_global.access_token == null) {
                    Epicture_global.myWebView.setVisibility(View.VISIBLE);
                } else {
                    GalleriesRequests(this.ctx);
                }
            }
        }
    }
}

