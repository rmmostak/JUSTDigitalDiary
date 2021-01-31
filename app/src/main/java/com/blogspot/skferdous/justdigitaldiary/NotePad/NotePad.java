package com.blogspot.skferdous.justdigitaldiary.NotePad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.NoteAdapter;
import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.NoteModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.blogspot.skferdous.justdigitaldiary.NotePad.MakeNote.NOTE_NODE;
import static com.blogspot.skferdous.justdigitaldiary.NotePad.ViewNote.ROOT_NOTE;

public class NotePad extends AppCompatActivity {
    private TextView notice;
    private RecyclerView recyclerView;
    private FloatingActionButton actionButton;
    private List<NoteModel> modelList = new ArrayList<>();
    private NoteAdapter adapter;

    private DatabaseReference reference;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pad);

        CoordinatorLayout coordinatorLayout;
        coordinatorLayout=findViewById(R.id.coordinator);
        if (!isConnected()) {
            /*new AlertDialog.Builder(this)
                    .setIcon(R.drawable.logo)
                    .setTitle("You are offline!")
                    .setMessage("Please connect to the internet and try again, Thank you!")
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    }).show();*/
            Snackbar.make(coordinatorLayout, "You don't have internet connection, Please connect!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", v -> {
                        return;
                    }).show();
        }

        notice = findViewById(R.id.notice);
        recyclerView = findViewById(R.id.recyclerView);
        actionButton = findViewById(R.id.actionButton);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        showNoteList();

        actionButton.setOnClickListener(view -> {
            Intent intent = new Intent(NotePad.this, MakeNote.class);
            startActivity(intent);
        });
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void showNoteList() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Data is retrieving, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
        String user = auth.getUid();
        reference = FirebaseDatabase.getInstance().getReference(ROOT_NOTE).child(NOTE_NODE).child(user);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NoteModel model = snapshot.getValue(NoteModel.class);
                    modelList.add(model);
                }
                if (modelList.isEmpty()) {
                    notice.setVisibility(View.VISIBLE);
                    dialog.dismiss();

                } else {
                    notice.setVisibility(View.GONE);
                    adapter = new NoteAdapter(NotePad.this, modelList);
                    recyclerView.setAdapter(adapter);
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NotePad.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NotePad.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        super.onBackPressed();
    }
}