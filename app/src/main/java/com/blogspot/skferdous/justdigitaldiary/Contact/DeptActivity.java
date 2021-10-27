package com.blogspot.skferdous.justdigitaldiary.Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactAdapter;
import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactCategoryAdapter;
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
import static com.blogspot.skferdous.justdigitaldiary.Contact.FacultyListActivity.FINAL_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Contact.FacultyListActivity.SECOND_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastShort;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class DeptActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference databaseReference, reference;
    private List<String> keyList;
    public static String THIRD_CHILD;
    public List<String> emailList = new ArrayList<>();
    public boolean role = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences preferences = getSharedPreferences("child", Context.MODE_PRIVATE);
        THIRD_CHILD = preferences.getString("third_child", null);
        role = preferences.getBoolean("role", false);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(THIRD_CHILD);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(DeptActivity.this);
        recyclerView.setLayoutManager(manager);

        keyList = new ArrayList<>();
        showContactList();
        sendMail();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (role) {
            getMenuInflater().inflate(R.menu.send_mail, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.sendEmail) {
            Log.d("email", emailList.toString());
            emailIntent(emailList);
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMail() {
        try {
            SharedPreferences preferences = getSharedPreferences("child", Context.MODE_PRIVATE);
            String child = preferences.getString("first_ref", null);

            reference = FirebaseDatabase.getInstance().getReference().child("Updated").child(ROOT).child(child);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        reference = FirebaseDatabase.getInstance().getReference().child("Updated").child(ROOT).child(child).child(Objects.requireNonNull(snapshot.getKey())).child(THIRD_CHILD);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot sn : dataSnapshot.getChildren()) {
                                    reference = FirebaseDatabase.getInstance().getReference().child("Updated").child(ROOT).child(child).child(Objects.requireNonNull(snapshot.getKey())).child(THIRD_CHILD).child(Objects.requireNonNull(sn.getKey()));
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            List<String> list = new ArrayList<>();
                                            for (DataSnapshot ss : dataSnapshot.getChildren()) {
                                                for (DataSnapshot sna : ss.getChildren()) {
                                                    ChildModel model = sna.getValue(ChildModel.class);
                                                    if (model != null) {
                                                        //Log.d("tag", model.getEmail());
                                                        if (model.getEmail().equals("") || model.getEmail().equals("Null")) {
                                                            continue;
                                                        } else if (model.getEmail().contains(" ")) {
                                                            list.add(model.getEmail().replace(" ", ""));
                                                        } else {
                                                            list.add(model.getEmail());
                                                        }
                                                    } else {
                                                        ToastLong(DeptActivity.this, "Sorry, Something went wrong. Please try again later!");
                                                    }
                                                }
                                            }
                                            emailList.addAll(list);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            ToastLong(DeptActivity.this, databaseError.getMessage());
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                ToastLong(DeptActivity.this, databaseError.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(DeptActivity.this, databaseError.getMessage());
                }
            });

        } catch (Exception e) {
            Toast.makeText(DeptActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void emailIntent(List<String> eList) {
        String sendTo = eList.toString().substring(1, eList.toString().length() - 1);
        Log.d("email", sendTo);
        if (sendTo.isEmpty()) {
            Toast.makeText(DeptActivity.this, "Sorry, email address is not found", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{sendTo});
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(Intent.createChooser(intent, "Send via..."));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(DeptActivity.this, "Sorry, email address is not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showContactList() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Data is retrieving, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        //Log.d("List", emailList.size() + "");
        try {
            SharedPreferences preferences = getSharedPreferences("child", Context.MODE_PRIVATE);
            String child = preferences.getString("first_ref", null);
            databaseReference = FirebaseDatabase.getInstance().getReference("Updated").child(ROOT).child(child);
            //ToastShort(this, THIRD_CHILD);
            databaseReference.keepSynced(true);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        databaseReference.child(snapshot.getKey()).child(THIRD_CHILD).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot sn : dataSnapshot.getChildren()) {
                                    for (DataSnapshot s : sn.getChildren()) {
                                        keyList.add(s.getKey());
                                    }
                                }
                                adapter = new ContactAdapter(keyList);
                                recyclerView.setAdapter(adapter);
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                ToastLong(DeptActivity.this, databaseError.getMessage());
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(DeptActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DeptActivity.this, ContactNode.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}