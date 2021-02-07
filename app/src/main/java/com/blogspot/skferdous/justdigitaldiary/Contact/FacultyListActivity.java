package com.blogspot.skferdous.justdigitaldiary.Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactViewAdapter;
import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.ChildModel;
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
import static com.blogspot.skferdous.justdigitaldiary.Contact.DeptActivity.THIRD_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class FacultyListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ChildModel> childModelList;
    private ContactViewAdapter adapter;
    private DatabaseReference databaseReference;
    public static String SECOND_CHILD = "";
    public static String FINAL_CHILD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_list);

        CoordinatorLayout coordinatorLayout;
        coordinatorLayout = findViewById(R.id.coordinator);
/*        if (!isConnected()) {
            Snackbar.make(coordinatorLayout, "You don't have internet connection, Please connect!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", v -> {
                        return;
                    }).show();
        }*/

        Intent intent = getIntent();
        String firstChild = intent.getStringExtra("firstChild");
        SECOND_CHILD = intent.getStringExtra("secondChild");
        FINAL_CHILD = intent.getStringExtra("finalChild");

        ActionBar bar = getSupportActionBar();
        bar.setTitle(FINAL_CHILD);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        childModelList = new ArrayList<>();
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
            databaseReference = FirebaseDatabase.getInstance().getReference(ROOT).child(FIRST_CHILD).child(THIRD_CHILD).child(FINAL_CHILD);
            databaseReference.keepSynced(true);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChildModel model = snapshot.getValue(ChildModel.class);
                        childModelList.add(model);
                    }

                    adapter = new ContactViewAdapter(FacultyListActivity.this, childModelList);
                    recyclerView.setAdapter(adapter);
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(FacultyListActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Toast.makeText(FacultyListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}