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
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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
import java.util.Objects;

import static com.blogspot.skferdous.justdigitaldiary.Contact.ContactNode.FIRST_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Contact.FacultyListActivity.SECOND_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastShort;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class ContactListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ChildModel> childModelList;
    private ContactViewAdapter adapter;
    private DatabaseReference databaseReference;
    public static String FINAL_CHILD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Intent intent = getIntent();
        SECOND_CHILD = intent.getStringExtra("secondChild");
        FINAL_CHILD = intent.getStringExtra("finalChild");

        ActionBar bar = getSupportActionBar();
        bar.setTitle(SECOND_CHILD);

        TextView itemDesc = findViewById(R.id.itemDesc);
        itemDesc.setText(SECOND_CHILD);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        childModelList = new ArrayList<>();
        makeChildList();
    }

    private void makeChildList() {
        try {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Loading, please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setButton("Cancel", (dialog1, which) -> startActivity(new Intent(ContactListActivity.this, ContactNode.class)));
            dialog.show();
            SharedPreferences preferences = getSharedPreferences("child", MODE_PRIVATE);
            SECOND_CHILD = preferences.getString("second_child", null);
            databaseReference = FirebaseDatabase.getInstance().getReference(ROOT).child(FIRST_CHILD);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        databaseReference.child(snapshot.getKey()).child(SECOND_CHILD).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    ChildModel model = snapshot.getValue(ChildModel.class);
                                    childModelList.add(model);
                                }

                                adapter = new ContactViewAdapter(ContactListActivity.this, childModelList);
                                recyclerView.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(ContactListActivity.this, databaseError.getMessage());
                }
            });
            dialog.dismiss();
        } catch (Exception e) {
            ToastLong(ContactListActivity.this, e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ContactListActivity.this, ContactNode.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }

}