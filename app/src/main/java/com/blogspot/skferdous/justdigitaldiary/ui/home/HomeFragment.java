package com.blogspot.skferdous.justdigitaldiary.ui.home;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.blogspot.skferdous.justdigitaldiary.Calendar.CalendarActivity;
import com.blogspot.skferdous.justdigitaldiary.Contact.ContactNode;
import com.blogspot.skferdous.justdigitaldiary.DocsActivity;
import com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity;
import com.blogspot.skferdous.justdigitaldiary.NotePad.NotePad;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.blogspot.skferdous.justdigitaldiary.VehicleTracking.MapsActivity;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        /*ImageButton docs=root.findViewById(R.id.docs);
        docs.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), DocsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });*/

        CardView adminContact = root.findViewById(R.id.adminContact);
        adminContact.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ContactNode.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SharedPreferences preferences = getActivity().getSharedPreferences("child", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("first_ref", "Administrative Offices");
            editor.commit();
            //intent.putExtra("first_ref", "Administrative Offices");
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });

        CardView facultyContact = root.findViewById(R.id.facultyContact);
        facultyContact.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ContactNode.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SharedPreferences preferences = getActivity().getSharedPreferences("child", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("first_ref", "Faculty Members");
            editor.commit();
            //intent.putExtra("first_ref", "Faculty Members");
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });

        CardView vehicle = root.findViewById(R.id.vehicleTracking);

         vehicle.setOnClickListener(v -> {
           AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Notice!");
            builder.setIcon(R.drawable.logo);
            builder.setMessage("This feature is almost done and coming soon with attractive features, thank you for your enduring!!");
            builder.setPositiveButton("OK", (dialog1, which) -> {
                dialog1.dismiss();
            });
            builder.show();
        });

        /*vehicle.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });*/

        CardView calendar = root.findViewById(R.id.calendar);
        calendar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CalendarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());

        });

        CardView notepad = root.findViewById(R.id.notepad);
        notepad.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotePad.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });

        CardView explore = root.findViewById(R.id.explore);
        explore.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ExploreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });

        /*CardView upComing = root.findViewById(R.id.upComing);
        upComing.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UpcomingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });*/

        return root;
    }
}