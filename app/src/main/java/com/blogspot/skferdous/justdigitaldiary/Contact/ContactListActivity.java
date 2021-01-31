package com.blogspot.skferdous.justdigitaldiary.Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import static com.blogspot.skferdous.justdigitaldiary.Contact.FacultyListActivity.SECOND_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class ContactListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ChildModel> childModelList;
    private ContactViewAdapter adapter;
    private DatabaseReference databaseReference;
    public static String FINAL_CHILD = "";
    String firstChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

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

        Intent intent = getIntent();
        firstChild = intent.getStringExtra("firstChild");
        SECOND_CHILD = intent.getStringExtra("secondChild");
        FINAL_CHILD = intent.getStringExtra("finalChild");

        ActionBar bar = getSupportActionBar();
        bar.setTitle(SECOND_CHILD);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference(ROOT).child(FIRST_CHILD).child(SECOND_CHILD);

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

        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChildModel model = snapshot.getValue(ChildModel.class);
                    childModelList.add(model);
                }

                adapter = new ContactViewAdapter(ContactListActivity.this, childModelList);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ContactListActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ContactListActivity.this, ContactNode.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("node", firstChild);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}