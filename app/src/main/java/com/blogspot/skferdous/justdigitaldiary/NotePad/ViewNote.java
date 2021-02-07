package com.blogspot.skferdous.justdigitaldiary.NotePad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.NoteModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.MakeNote.NOTE_NODE;

public class ViewNote extends AppCompatActivity {

    private TextView title, body;
    private DatabaseReference reference;
    private String note_id;
    public static final String ROOT_NOTE = "JUST Digital Diary Notes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        CoordinatorLayout coordinatorLayout;
        coordinatorLayout = findViewById(R.id.coordinator);

/*
        if (!isConnected()) {
            Snackbar.make(coordinatorLayout, "You don't have internet connection, Please connect!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", v -> {
                        return;
                    }).show();
        }
*/

        Intent intent = getIntent();
        note_id = intent.getStringExtra("noteId");

        title = findViewById(R.id.noteTitle);
        body = findViewById(R.id.noteBody);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Data is retrieving, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        try {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String user = auth.getUid();

            reference = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child(NOTE_NODE).child(user).child(note_id);
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

    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
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

                    NoteModel model = new NoteModel(note_id, date, time, stTitle, stBody);

                    try {
                        reference.setValue(model).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Intent intent = new Intent(ViewNote.this, NotePad.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left2right, R.anim.right2left);
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
                    reference.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Intent intent = new Intent(ViewNote.this, NotePad.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left2right, R.anim.right2left);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewNote.this, NotePad.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left2right, R.anim.right2left);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}