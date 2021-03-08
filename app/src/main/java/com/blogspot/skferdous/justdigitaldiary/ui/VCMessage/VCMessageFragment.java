package com.blogspot.skferdous.justdigitaldiary.ui.VCMessage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blogspot.skferdous.justdigitaldiary.R;

public class VCMessageFragment extends Fragment {

    //private VCMessageModel VCMessageModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*VCMessageModel =
                new ViewModelProvider(this).get(VCMessageModel.class);*/
        View root = inflater.inflate(R.layout.fragment_vc_message, container, false);
        /*final TextView textView = root.findViewById(R.id.text_slideshow);
        VCMessageModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
}