package com.blogspot.skferdous.justdigitaldiary.NotePad;

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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.NoteModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.ViewNote.ROOT_NOTE;

public class MakeNote extends AppCompatActivity {

    private EditText noteTitle, noteBody;
    public static final String NOTE_NODE = "Notes";
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_note);

        CoordinatorLayout coordinatorLayout;
        coordinatorLayout = findViewById(R.id.coordinator);
/*        if (!isConnected()) {
            Snackbar.make(coordinatorLayout, "You don't have internet connection, Please connect!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", v -> {
                        return;
                    }).show();
        }*/

        noteTitle = findViewById(R.id.noteTitle);
        noteBody = findViewById(R.id.noteBody);
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public String getDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String strDate = formatter.format(date);

        return strDate;
    }

    public String getTime() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        String strTime = formatter.format(date);

        return strTime;
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

            String title = noteTitle.getText().toString().trim();
            String body = noteBody.getText().toString().trim();
            String time = getTime();
            String date = getDate();

            if (!TextUtils.isEmpty(title)) {

                if (!TextUtils.isEmpty(body)) {
                    ProgressDialog dialog = new ProgressDialog(this);
                    dialog.setTitle("Note is saving, please wait...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    dialog.show();

                    try {
                        String user = auth.getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child(NOTE_NODE).child(user);
                        reference.keepSynced(true);
                        String key = reference.push().getKey();
                        NoteModel model = new NoteModel(key, date, time, title, body);
                        reference.child(key).setValue(model).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(MakeNote.this, NotePad.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left2right, R.anim.right2left);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MakeNote.this, NotePad.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left2right, R.anim.right2left);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}