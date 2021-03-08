package com.blogspot.skferdous.justdigitaldiary.NotePad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Script;
import android.widget.TextView;

import com.blogspot.skferdous.justdigitaldiary.Adapter.InviteAdapter;
import com.blogspot.skferdous.justdigitaldiary.Adapter.NoteAdapter;
import com.blogspot.skferdous.justdigitaldiary.Model.InvitedModel;
import com.blogspot.skferdous.justdigitaldiary.Model.NoteModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.cache.DiskLruCache;

import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;

public class InvitedNotePad extends AppCompatActivity {

    private TextView notice;
    private RecyclerView recyclerView;
    private FloatingActionButton actionButton;
    private List<InvitedModel> modelList = new ArrayList<>();
    private InviteAdapter adapter;
    public static final String NOTE_ROOT = "JUST Digital Diary Notes";
    public static final String NOTE_INVITED = "Invitations";

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invited_note_pad);

        notice = findViewById(R.id.notice);
        recyclerView = findViewById(R.id.recyclerView);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        showNoteList();
    }

    private void showNoteList() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Data is retrieving, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();

        try {
            modelList.clear();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(NOTE_ROOT).child(NOTE_INVITED).child(auth.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        InvitedModel model = snapshot.getValue(InvitedModel.class);
                        modelList.add(model);
                        //getNotes(model.getNoteId());
                    }
                    adapter = new InviteAdapter(InvitedNotePad.this, modelList);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(InvitedNotePad.this, databaseError.getMessage());
                }
            });
            dialog.dismiss();
        } catch (Exception e) {
            ToastLong(InvitedNotePad.this, e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(InvitedNotePad.this, NotePad.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}