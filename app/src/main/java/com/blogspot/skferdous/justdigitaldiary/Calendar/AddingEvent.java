package com.blogspot.skferdous.justdigitaldiary.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.R;

public class AddingEvent extends AppCompatActivity {

    private EditText title, desc, location;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_event);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Alert!")
                .setIcon(R.drawable.logo)
                .setMessage("This page is under constructing..")
                .setPositiveButton("OK", ((dialog, which) -> {
                    return;
                }));
        builder.show();

        title = findViewById(R.id.et_title);
        desc = findViewById(R.id.et_desc);
        location = findViewById(R.id.et_loc);
        save = findViewById(R.id.saveEvent);

        save.setOnClickListener(v -> {
            String title_st = title.getText().toString().trim();
            String desc_st = desc.getText().toString().trim();
            String location_st = location.getText().toString().trim();

            if (!title_st.isEmpty()) {
                if (!desc_st.isEmpty()) {
                    if (!location_st.isEmpty()) {

                        Intent intent = new Intent(Intent.ACTION_INSERT);
                        intent.setData(CalendarContract.Events.CONTENT_URI);
                        intent.putExtra(CalendarContract.Events.TITLE, title_st);
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, desc_st);
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location_st);
                        intent.putExtra(CalendarContract.Events.ALL_DAY, true);

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Successfully saved your event!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Sorry, your event saved!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        title.setError("Enter event location.");
                        title.requestFocus();
                    }
                } else {
                    desc.setError("Enter event description.");
                    desc.requestFocus();
                }
            } else {
                title.setError("Enter event title.");
                title.requestFocus();
            }
        });
    }
}