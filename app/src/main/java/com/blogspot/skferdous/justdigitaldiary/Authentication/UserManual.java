package com.blogspot.skferdous.justdigitaldiary.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blogspot.skferdous.justdigitaldiary.R;

import okhttp3.Callback;

public class UserManual extends AppCompatActivity {

    Integer pageNumber = 0;
    private WebView webView;
    String pdf_url="https://firebasestorage.googleapis.com/v0/b/just-bus-tracking-5500d.appspot.com/o/files%2Fuser_manual.pdf?alt=media&token=b522a067-adb3-4841-bb73-eea26a46f175";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);
        webView = findViewById(R.id.webView);
        /*pdfView.fromAsset("user_manual.pdf")
                .defaultPage(pageNumber)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .load();*/
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        //webView.setWebViewClient(new Callback());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://drive.google.com/file/d/1Fi5slwwur4PcHa9b8yX6jxIl6n8ZgKET/view?usp=sharing");

        //setContentView(webView);
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return (false);
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserManual.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }

}