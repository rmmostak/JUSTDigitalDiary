package com.blogspot.skferdous.justdigitaldiary.ui.about;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blogspot.skferdous.justdigitaldiary.BuildConfig;
import com.blogspot.skferdous.justdigitaldiary.R;

public class AboutUsFragment extends Fragment {

    //private AboutUsModel aboutUsModel;
    private StringBuilder builder = new StringBuilder();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*aboutUsModel =
                new ViewModelProvider(this).get(AboutUsModel.class);*/
        View root = inflater.inflate(R.layout.fragment_about_us, container, false);
        TextView version = root.findViewById(R.id.version);
        String versionCode = String.valueOf(BuildConfig.VERSION_CODE);
        if (versionCode.length() > 1) {
            for (int i = 0; i < versionCode.length(); i++) {
                builder.append(".");
                builder.append(versionCode.charAt(i));
            }
        }
        version.setText(BuildConfig.VERSION_NAME + builder);
        return root;
    }
}