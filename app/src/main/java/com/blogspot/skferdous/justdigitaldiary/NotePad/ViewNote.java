package com.blogspot.skferdous.justdigitaldiary.NotePad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.InvitedModel;
import com.blogspot.skferdous.justdigitaldiary.Model.NoteModel;
import com.blogspot.skferdous.justdigitaldiary.NotePad.Fragment.MyNoteFragment;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.NOTE_ROOT;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.MakeNote.NOTE_NODE;

public class ViewNote extends AppCompatActivity {

    private TextView title, body;
    private DatabaseReference reference;
    private String note_id, push, uid;
    ;
    private RecyclerView recyclerView;
    public static final String ROOT_NOTE = "JUST Digital Diary Notes";
    public static final String NOTE_INVITE = "Invitations";
    private String attendees = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        title = findViewById(R.id.noteTitle);
        body = findViewById(R.id.noteBody);
        recyclerView = findViewById(R.id.emailRecycler);


        Intent intent = getIntent();
        note_id = intent.getStringExtra("noteId");
        int count = intent.getIntExtra("count", 0);

        if (count > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setLayoutManager(new LinearLayoutManager(ViewNote.this));
            showRecyclerView();
        }

        showPersonalNote();
    }

    private void showRecyclerView() {
        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            //String email = auth.getCurrentUser().getEmail();
            //DatabaseReference reference = FirebaseDatabase.getInstance().getReference(NOTE_ROOT).child(NOTE_NODE).child();

        } catch (Exception e) {
            ToastLong(ViewNote.this, e.getMessage());
        }
    }

    private void showPersonalNote() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Data is retrieving, please wait...");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            reference = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child(NOTE_NODE).child(auth.getUid()).child(note_id);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    NoteModel model = dataSnapshot.getValue(NoteModel.class);
                    if (model.getTitle().isEmpty()) {
                        title.setText("");
                        return;
                    } else {
                        title.setText(model.getTitle());
                    }

                    if (model.getBody().isEmpty()) {
                        body.setText("");
                        return;
                    } else {
                        body.setText(model.getBody());
                    }
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ViewNote.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Toast.makeText(ViewNote.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_save_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.edit) {

            title.setFocusableInTouchMode(true);
            title.requestFocus();

            body.setFocusableInTouchMode(true);
            title.requestFocus();

            return false;
        }

        if (id == R.id.save) {

            MakeNote noteActivity = new MakeNote();
            String date = noteActivity.getDate();
            String time = noteActivity.getTime();
            String stTitle = title.getText().toString().trim();
            String stBody = body.getText().toString().trim();

            if (!stTitle.equals("")) {

                if (!stBody.equals("")) {
                    ProgressDialog dialog = new ProgressDialog(this);
                    dialog.setTitle("Note is saving, please wait...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    dialog.show();

                    try {
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                NoteModel noteModel = dataSnapshot.getValue(NoteModel.class);
                                attendees = noteModel != null ? noteModel.getAttendees() : "null";
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child(NOTE_NODE).child(auth.getUid()).child(note_id);
                                NoteModel model = new NoteModel(note_id, date, time, stTitle, stBody, attendees, false);
                                try {
                                    databaseReference.setValue(model).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(ViewNote.this, NotePad.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                            startActivity(intent, options.toBundle());
                                            finish();

                                        } else {
                                            Toast.makeText(ViewNote.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }
                                    });
                                } catch (Exception e) {
                                    Toast.makeText(ViewNote.this, e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                ToastLong(ViewNote.this, databaseError.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        ToastLong(ViewNote.this, e.getMessage());
                    }

                } else {
                    body.setError("You have to add something!!");
                    body.requestFocus();
                }

            } else {
                title.setError("Please enter a note title!");
                title.requestFocus();
            }


            return false;
        }

        if (id == R.id.delete) {

            deleteThis();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteThis() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Note is deleting, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to delete this note?");
        builder.setTitle("Alert:");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {

            dialog.show();
            try {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child(NOTE_NODE).child(auth.getUid()).child(note_id);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            NoteModel model = dataSnapshot.getValue(NoteModel.class);

                            try {
                                if (model != null) {
                                    uid = model.getAttendees();
                                }
                            } catch (DatabaseException e) {
                                e.printStackTrace();
                            }

                            StringBuilder builder1 = new StringBuilder();
                            if (uid.equals("null")) {
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child(NOTE_NODE).child(auth.getUid()).child(note_id);
                                database.removeValue().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Intent intent = new Intent(ViewNote.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                        startActivity(intent, options.toBundle());
                                        finish();

                                    } else {
                                        ToastLong(ViewNote.this, task1.getException().getMessage());
                                    }
                                });
                            } else {
                                for (int i = 0; i < uid.length(); i++) {
                                    builder1.append(uid.charAt(i));
                                    if (uid.charAt(i) == ',') {
                                        deleteNode(builder1.substring(0, builder1.length() - 1));
                                        builder1.delete(0, builder1.length());
                                    }
                                }
                            }
                        } catch (DatabaseException e) {
                            ToastLong(ViewNote.this, e.getMessage());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ToastLong(ViewNote.this, databaseError.getMessage());

                    }
                });
            } catch (Exception e) {
                ToastLong(ViewNote.this, e.getMessage());
            }
            dialog.dismiss();
            Intent intent = new Intent(ViewNote.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
            finish();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {
                /*Intent intent = new Intent(ViewNote.this, NotePad.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                dialog.dismiss();*/
            return;
        });

        builder.show();
    }

    private void deleteNode(String substring) {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(NOTE_ROOT).child(NOTE_INVITE).child(substring);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        InvitedModel model = snapshot.getValue(InvitedModel.class);
                        if (note_id.equals(model.getNoteId())) {
                            push = model.getKey();
                            //ToastLong(ViewNote.this, push);
                            finalDelete(push, substring);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(ViewNote.this, databaseError.getMessage());
                }
            });

        } catch (Exception e) {
            ToastLong(ViewNote.this, e.getMessage());
        }
    }

    private void finalDelete(String push, String subString) {
        try {
            //ToastLong(ViewNote.this, subString+"\n"+push);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(NOTE_ROOT).child(NOTE_INVITE).child(subString).child(push);
            reference.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child(NOTE_NODE).child(auth.getUid()).child(note_id);
                    database.removeValue().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            return;
                        } else {
                            ToastLong(ViewNote.this, task.getException().getMessage());
                        }
                    });
                }
            });
            //ToastLong(ViewNote.this, "Please reload this page to get current data!");
        } catch (Exception e) {
            ToastLong(ViewNote.this, e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewNote.this, NotePad.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}