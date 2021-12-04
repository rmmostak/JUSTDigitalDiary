package com.blogspot.skferdous.justdigitaldiary.ui.about;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blogspot.skferdous.justdigitaldiary.BuildConfig;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AboutUsFragment extends Fragment {

    //private AboutUsModel aboutUsModel;
    private StringBuilder builder = new StringBuilder();
    private FloatingActionButton contact, share;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_about_us, container, false);
        TextView version = root.findViewById(R.id.version);
        contact = root.findViewById(R.id.contact);
        share = root.findViewById(R.id.share);

        contact.setOnClickListener(view -> makeCall());
        share.setOnClickListener(view -> makeShare());

        String versionCode = String.valueOf(BuildConfig.VERSION_CODE);
        versionCode.length();
        for (int i = 0; i < versionCode.length(); i++) {
            builder.append(".");
            builder.append(versionCode.charAt(i));
        }
        version.setText(BuildConfig.VERSION_NAME + builder);
        return root;
    }

    private void makeShare() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareBody = "https://play.google.com/store/apps/details?id=com.blogspot.skferdous.justdigitaldiary";
        String shareSubject = "JUST Digital Diary Android App";

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        startActivity(Intent.createChooser(shareIntent, "Share App Using..."));
    }

    private void makeCall() {
        String no = "+8801780891662";
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + no));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(context, "Unissued call permission!", Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 10);
                return;
            }
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        } else {
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        }

    }
}