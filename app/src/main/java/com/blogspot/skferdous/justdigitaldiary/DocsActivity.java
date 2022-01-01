package com.blogspot.skferdous.justdigitaldiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DocsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs);

/*        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        webView = findViewById(R.id.docsView);
        /*pdfView.fromAsset("user_manual.pdf")
                .defaultPage(pageNumber)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .load();

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Data is retrieving, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
        Runnable r = () -> {
            dialog.dismiss();
        };
        Handler handler = new Handler();
        handler.postDelayed(r, 4000);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        //webView.setWebViewClient(new Callback());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://drive.google.com/file/d/1Fi5slwwur4PcHa9b8yX6jxIl6n8ZgKET/view?usp=sharing");


        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        webView = findViewById(R.id.docsView);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Data is retrieving, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();

        Runnable r = () -> {

            dialog.dismiss();
        };

        Handler handler = new Handler();
        handler.postDelayed(r, 4000);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://just.edu.bd");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        dialog.dismiss();*/
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(DocsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();

    }
}