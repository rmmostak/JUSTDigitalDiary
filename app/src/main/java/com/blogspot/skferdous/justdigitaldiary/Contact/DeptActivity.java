package com.blogspot.skferdous.justdigitaldiary.Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactAdapter;
import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.NotePad.MakeNote;
import com.blogspot.skferdous.justdigitaldiary.NotePad.NotePad;
import com.blogspot.skferdous.justdigitaldiary.NotePad.ViewNote;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.blogspot.skferdous.justdigitaldiary.Contact.ContactNode.FIRST_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Contact.FacultyListActivity.FINAL_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Contact.FacultyListActivity.SECOND_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class DeptActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference databaseReference;
    private List<String> keyList;
    public static String THIRD_CHILD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept);

        CoordinatorLayout coordinatorLayout;
        coordinatorLayout = findViewById(R.id.coordinator);
/*        if (!isConnected()) {
            Snackbar.make(coordinatorLayout, "You don't have internet connection, Please connect!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", v -> {
                        return;
                    }).show();
        }*/

        Intent intent = getIntent();
        THIRD_CHILD = intent.getStringExtra("thirdChild");

        ActionBar bar = getSupportActionBar();
        bar.setTitle(THIRD_CHILD);

        recyclerView = findViewById(R.id.recyclerView);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            GridLayoutManager manager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
        } else {
            GridLayoutManager manager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
        }
        if (savedInstanceState != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference(ROOT).child(FIRST_CHILD).child(THIRD_CHILD);
        }

        keyList = new ArrayList<>();
        showContactList();
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void showContactList() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Data is retrieving, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        try {
            databaseReference = FirebaseDatabase.getInstance().getReference(ROOT).child(FIRST_CHILD).child(THIRD_CHILD);
            databaseReference.keepSynced(true);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String ch = snapshot.getKey();
                        keyList.add(ch);
                    }
                    adapter = new ContactAdapter(keyList);
                    recyclerView.setAdapter(adapter);
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DeptActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Toast.makeText(DeptActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}