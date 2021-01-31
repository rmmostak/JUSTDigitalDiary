package com.blogspot.skferdous.justdigitaldiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    private TextView welcome, copy;
    private ImageView event_image, just_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash_screen);

        welcome = findViewById(R.id.welcome);
        copy = findViewById(R.id.copy);
        event_image = findViewById(R.id.event_image);
        just_logo=findViewById(R.id.just_logo);

        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        welcome.startAnimation(animation);

        Animation animation1= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        copy.startAnimation(animation1);

        Animation animation2= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.left2right);
        event_image.startAnimation(animation2);

        Animation animation3= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.right2left);
        just_logo.startAnimation(animation3);

        Handler handler= new Handler();
        handler.postDelayed(r,2000);
    }

    Runnable r = () -> {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        ActivityOptions options=ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left2right, R.anim.right2left);
        startActivity(intent, options.toBundle());
        finish();
    };
}