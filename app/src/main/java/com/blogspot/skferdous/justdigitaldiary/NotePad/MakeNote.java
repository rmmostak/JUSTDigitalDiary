package com.blogspot.skferdous.justdigitaldiary.NotePad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.AttendeeAdapter;
import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.AuthModel;
import com.blogspot.skferdous.justdigitaldiary.Model.InvitedModel;
import com.blogspot.skferdous.justdigitaldiary.Model.NoteModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.blogspot.skferdous.justdigitaldiary.Authentication.SignupActivity.checkEmailValidity;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.ViewNote.ROOT_NOTE;

public class MakeNote extends AppCompatActivity {

    private EditText noteTitle, noteBody, emailText;
    private RecyclerView emailRecycler;
    private Button add;
    public static final String NOTE_NODE = "Notes";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private AttendeeAdapter adapter;
    private List<String> nameList = new ArrayList<>();
    private List<String> emailList = new ArrayList<>();
    private List<String> uidList = new ArrayList<>();
    private List<String> checkList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_note);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        noteTitle = findViewById(R.id.noteTitle);
        noteBody = findViewById(R.id.noteBody);
        emailText = findViewById(R.id.emailText);
        emailRecycler = findViewById(R.id.emailRecycler);
        add = findViewById(R.id.emailAdd);

        emailRecycler.setLayoutManager(new LinearLayoutManager(MakeNote.this));

        add.setOnClickListener(v -> {
            String email = emailText.getText().toString().trim();
            if (!email.isEmpty()) {
                String check = null;
                if (checkEmailValidity(email).equals("just.edu.bd")) {
                    check = "Faculty and Stuff";
                } else if (checkEmailValidity(email).equals("student.just.edu.bd")) {
                    check = "Students";
                } else {
                    ToastLong(MakeNote.this, "Please enter institutional email.");
                }

                emailRecycler.setVisibility(View.VISIBLE);

                try {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(check);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot sn : snapshot.getChildren()) {
                                    AuthModel model = sn.getValue(AuthModel.class);
                                    if (email.equals(model.getEmail())) {
                                        nameList.add(model.getName());
                                        emailList.add(model.getEmail());
                                        uidList.add(model.getUid());
                                        checkList.add(model.getUid());
                                    }
                                }
                            }
                            if (checkList.isEmpty()) {
                                ToastLong(MakeNote.this, "Sorry, Your requested email is not found!");
                            }
                            checkList.clear();
                            adapter = new AttendeeAdapter(MakeNote.this, nameList);
                            emailRecycler.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            ToastLong(MakeNote.this, databaseError.getMessage());
                        }
                    });
                } catch (Exception e) {
                    ToastLong(MakeNote.this, e.getMessage());
                }

                //emailRecycler.notifyAll();
                emailText.setText("");
            } else {
                emailText.setError("Please enter a valid email!");
                emailText.requestFocus();
            }
        });
    }

    public String getDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        return formatter.format(date);
    }

    public String getTime() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

        return formatter.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.saveNote) {

            SaveNote();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SaveNote() {
        String title = noteTitle.getText().toString().trim();
        String body = noteBody.getText().toString().trim();
        String time = getTime();
        String date = getDate();

        if (!TextUtils.isEmpty(title)) {

            if (!TextUtils.isEmpty(body)) {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Note is saving, please wait...");
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.show();

                try {
                    String user = auth.getUid();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child(NOTE_NODE).child(user);

                    reference.keepSynced(true);
                    String key = reference.push().getKey();
                    String attendees = "";
                    if (!uidList.isEmpty()) {
                        for (int i = 0; i < uidList.size(); i++) {
                            attendees = attendees + uidList.get(i) + ",";
                        }
                        //attendees = attendees.substring(0, attendees.length());
                    } else {
                        attendees = "null";
                    }
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < nameList.size(); i++) {
                        builder.append(nameList.get(i)).append(", ");
                    }
                    for (int i = 0; i < uidList.size(); i++) {
                        makeShareNote(uidList.get(i), key, builder.toString(), false);
                    }
                    //ToastLong(this, "Size: " + nameList.size());
                    NoteModel model = new NoteModel(key, date, time, title, body, attendees, false);
                    reference.child(key).setValue(model).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(MakeNote.this, NotePad.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                            startActivity(intent, options.toBundle());
                            finish();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MakeNote.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(MakeNote.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                noteBody.setError("You have to add something!!");
                noteBody.requestFocus();
            }

        } else {
            noteTitle.setError("Please enter a note title!");
            noteTitle.requestFocus();
        }
    }

    private void makeShareNote(String s, String key, String atnd, boolean per) {
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String child;
            if (checkEmailValidity(auth.getCurrentUser().getEmail()).equals("just.edu.bd")) {
                child = "Faculty and Stuff";
            } else {
                child = "Students";
            }
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(child).child(auth.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AuthModel model = snapshot.getValue(AuthModel.class);
                        finalShareStep(model.getName(), auth.getUid(), key, s, atnd, per);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(MakeNote.this, databaseError.getMessage());
                }
            });

        } catch (Exception e) {
            ToastLong(MakeNote.this, e.getMessage());
        }
    }

    private void finalShareStep(String name, String id, String key, String s, String atnd, boolean per) {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child("Invitations").child(s);
            String push = databaseReference.push().getKey();
            InvitedModel model1 = new InvitedModel(push, key, id, atnd, name, per);
            databaseReference.child(push).setValue(model1);
        } catch (Exception e) {
            ToastLong(MakeNote.this, e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!noteTitle.getText().toString().isEmpty() || !noteBody.getText().toString().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MakeNote.this)
                    .setTitle("Alert!")
                    .setIcon(R.drawable.logo)
                    .setMessage("Would you like to save the edited note?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SaveNote();
                        }
                    }).setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MakeNote.this, NotePad.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                            startActivity(intent, options.toBundle());
                        }
                    });
            builder.show();

        }

    }
}