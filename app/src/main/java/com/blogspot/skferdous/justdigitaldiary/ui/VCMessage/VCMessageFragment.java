package com.blogspot.skferdous.justdigitaldiary.ui.VCMessage;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blogspot.skferdous.justdigitaldiary.R;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class VCMessageFragment extends Fragment {

    //private VCMessageModel VCMessageModel;

    @SuppressLint("WrongConstant")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*VCMessageModel =
                new ViewModelProvider(this).get(VCMessageModel.class);*/
        View root = inflater.inflate(R.layout.fragment_vc_message, container, false);

        //final TextView textView = root.findViewById(R.id.vcMessage);

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //textView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        //}

        return root;
    }
}