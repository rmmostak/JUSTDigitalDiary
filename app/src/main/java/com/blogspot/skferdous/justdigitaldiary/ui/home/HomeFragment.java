package com.blogspot.skferdous.justdigitaldiary.ui.home;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.blogspot.skferdous.justdigitaldiary.Calendar.CalendarActivity;
import com.blogspot.skferdous.justdigitaldiary.Contact.ContactActivity;
import com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity;
import com.blogspot.skferdous.justdigitaldiary.NotePad.NotePad;
import com.blogspot.skferdous.justdigitaldiary.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        CardView contact = root.findViewById(R.id.contact);
        contact.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ContactActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options=ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.left2right, R.anim.right2left);
            startActivity(intent, options.toBundle());
        });

        CardView calendar = root.findViewById(R.id.calendar);
        calendar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CalendarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options=ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.left2right, R.anim.right2left);
            startActivity(intent, options.toBundle());

        });

        CardView notepad = root.findViewById(R.id.notepad);
        notepad.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotePad.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options=ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.left2right, R.anim.right2left);
            startActivity(intent, options.toBundle());
        });

        CardView explore = root.findViewById(R.id.explore);
        explore.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ExploreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options=ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.left2right, R.anim.right2left);
            startActivity(intent, options.toBundle());
        });

        return root;
    }
}