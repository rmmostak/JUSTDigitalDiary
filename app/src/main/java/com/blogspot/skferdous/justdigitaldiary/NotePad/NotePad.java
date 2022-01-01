package com.blogspot.skferdous.justdigitaldiary.NotePad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.blogspot.skferdous.justdigitaldiary.Adapter.ViewPagerAdapter;
import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.NotePad.Fragment.MyNoteFragment;
import com.blogspot.skferdous.justdigitaldiary.NotePad.Fragment.SharedNoteFragment;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.material.tabs.TabLayout;

public class NotePad extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton actionButton;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pad);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(NotePad.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        actionButton=findViewById(R.id.actionButton);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new MyNoteFragment(), "My Notes");
        adapter.AddFragment(new SharedNoteFragment(), "Shared Notes");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        actionButton.setOnClickListener(view -> {
            Intent intent = new Intent(NotePad.this, MakeNote.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(NotePad.this, R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NotePad.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }

}