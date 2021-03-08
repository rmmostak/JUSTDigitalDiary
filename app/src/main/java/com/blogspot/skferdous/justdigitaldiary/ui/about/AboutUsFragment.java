package com.blogspot.skferdous.justdigitaldiary.ui.about;

import android.annotation.SuppressLint;
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

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*aboutUsModel =
                new ViewModelProvider(this).get(AboutUsModel.class);*/
        View root = inflater.inflate(R.layout.fragment_about_us, container, false);
        TextView version = root.findViewById(R.id.version);
        version.setText(BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE);
        return root;
    }
}