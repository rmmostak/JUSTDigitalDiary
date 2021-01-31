package com.blogspot.skferdous.justdigitaldiary.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.blogspot.skferdous.justdigitaldiary.R;

public class AboutUsFragment extends Fragment {

    private AboutUsModel aboutUsModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        aboutUsModel =
                new ViewModelProvider(this).get(AboutUsModel.class);
        View root = inflater.inflate(R.layout.fragment_about_us, container, false);
        /*final TextView textView = root.findViewById(R.id.text_gallery);
        aboutUsModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
}