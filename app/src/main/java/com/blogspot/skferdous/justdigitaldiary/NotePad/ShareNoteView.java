package com.blogspot.skferdous.justdigitaldiary.NotePad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.blogspot.skferdous.justdigitaldiary.Model.NoteModel;
import com.blogspot.skferdous.justdigitaldiary.NotePad.Fragment.SharedNoteFragment;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.NOTE_ROOT;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.MakeNote.NOTE_NODE;

public class ShareNoteView extends AppCompatActivity {

    private TextView title, body, sharedTitle;
    private DatabaseReference reference;
    private String note_id, inNoteId, inUserId;
    private boolean permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_note_view);

        Intent intent = getIntent();
        inNoteId = intent.getStringExtra("inNoteId");
        inUserId = intent.getStringExtra("inUserId");
        permission = intent.getBooleanExtra("inPermission", false);
        String senderName = intent.getStringExtra("inSenderName");

        ActionBar bar = getSupportActionBar();
        bar.setTitle(senderName + "'s Note");

        title = findViewById(R.id.noteTitle);
        body = findViewById(R.id.noteBody);
        sharedTitle = findViewById(R.id.sharedTitle);

        showSharedNote(inUserId, inNoteId);
    }

    private void showSharedNote(String inUserId, String inNoteId) {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(NOTE_ROOT).child(NOTE_NODE).child(inUserId).child(inNoteId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    NoteModel model = dataSnapshot.getValue(NoteModel.class);
                    title.setText(model.getTitle());
                    body.setText(model.getBody());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(ShareNoteView.this, databaseError.getMessage());
                }
            });
        } catch (Exception e) {
            ToastLong(ShareNoteView.this, e.getMessage());
        }
    }
}