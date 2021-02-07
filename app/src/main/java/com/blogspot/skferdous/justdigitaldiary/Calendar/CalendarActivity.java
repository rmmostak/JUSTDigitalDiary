package com.blogspot.skferdous.justdigitaldiary.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.NotePad.NotePad;
import com.blogspot.skferdous.justdigitaldiary.NotePad.ViewNote;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.agenda.AgendaEventView;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.blogspot.skferdous.justdigitaldiary.NotePad.MakeNote.NOTE_NODE;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.ViewNote.ROOT_NOTE;

public class CalendarActivity extends AppCompatActivity implements CalendarPickerController {

    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private String uid;

    private AgendaCalendarView mAgendaCalendarView;

    public long longValue = 0;
    public int intVal;
    public Calendar calendarT, calendarU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mAgendaCalendarView=findViewById(R.id.agenda_calendar_view);

        firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getUid();
        reference = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child(NOTE_NODE).child(uid);

        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -3);
        minDate.set(Calendar.DAY_OF_MONTH, 2);
        maxDate.add(Calendar.YEAR, 1);

        List<CalendarEvent> eventList = new ArrayList<>();

        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);

        AlertDialog.Builder builder=new AlertDialog.Builder(this)
        .setIcon(R.drawable.logo)
                .setTitle("Notice")
                .setMessage("This feature is under constructing, please keep using to get update!")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left2right, R.anim.right2left);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.addEvent) {
            //adding event to calendar

        }
        return super.onOptionsItemSelected(item);

    }


    //calendar picker dialog
    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(CalendarEvent event) {

    }

    @Override
    public void onScrollToDate(Calendar calendar) {

    }
}