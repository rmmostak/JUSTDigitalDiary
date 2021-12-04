package com.blogspot.skferdous.justdigitaldiary.Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.AdminFinalAdapter;
import com.blogspot.skferdous.justdigitaldiary.Model.AdminModel;
import com.blogspot.skferdous.justdigitaldiary.Model.ChildModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.blogspot.skferdous.justdigitaldiary.Contact.ContactNode.FIRST_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class SecondaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference databaseReference;
    private List<String> keyList;
    public static String ADMIN_CHILD;
    public String count = null, path = null;
    public boolean role = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences preferences = getSharedPreferences("child", Context.MODE_PRIVATE);
        ADMIN_CHILD = preferences.getString("second_child", null);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(ADMIN_CHILD);

        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin");
            FirebaseAuth auth = FirebaseAuth.getInstance();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            AdminModel model = sn.getValue(AdminModel.class);

                            assert model != null;
                            if (model.getId().equals(auth.getUid())) {
                                if (model.getIdentifier().equals("all") || model.getIdentifier().equals("Editor")) {
                                    role = true;
                                    return;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SecondaryActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(SecondaryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(SecondaryActivity.this);
        recyclerView.setLayoutManager(manager);

        keyList = new ArrayList<>();
        showContactList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (role) {
            getMenuInflater().inflate(R.menu.admin_edit, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.adminEdit) {
            addNewChild();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void addNewChild() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_dept, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Add a Category");
        dialogBuilder.setIcon(R.drawable.logo);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        EditText deptName = dialogView.findViewById(R.id.deptName);
        Button deptNext = dialogView.findViewById(R.id.deptNext);
        Button deptCancel = dialogView.findViewById(R.id.deptCancel);
        TextView hint = dialogView.findViewById(R.id.deptHint);
        hint.setText("Please enter a category name like- 'Office of the Vice Chancellor'");

        deptCancel.setOnClickListener(view1 -> alertDialog.dismiss());
        deptNext.setOnClickListener(view1 -> {
            String deptSt = deptName.getText().toString().trim();
            if (deptSt.isEmpty()) {
                deptName.setError("Please enter a category name!");
                deptName.requestFocus();
                return;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater1 = getLayoutInflater();
                View view2 = inflater1.inflate(R.layout.child_update_layout, null);
                builder.setView(view2);

                builder.setTitle("Add Person");
                builder.setIcon(R.drawable.logo);

                EditText name = view2.findViewById(R.id.name);
                EditText desg = view2.findViewById(R.id.designation);
                EditText email = view2.findViewById(R.id.email);
                EditText phoneHome = view2.findViewById(R.id.phoneHome);
                EditText phonePer = view2.findViewById(R.id.phonePer);
                EditText pbx = view2.findViewById(R.id.pbx);
                EditText others = view2.findViewById(R.id.others);
                Button update = view2.findViewById(R.id.update);
                Button delete = view2.findViewById(R.id.delete);
                Button cancel = view2.findViewById(R.id.cancel);

                update.setText("Add");
                delete.setVisibility(View.GONE);

                AlertDialog dialog = builder.create();
                dialog.show();

                cancel.setOnClickListener(view3 -> dialog.dismiss());
                update.setOnClickListener(view3 -> {
                    String nameSt = name.getText().toString().trim();
                    String desgSt = desg.getText().toString().trim();
                    String emailSt = email.getText().toString().trim();
                    String phoneHomeSt = phoneHome.getText().toString().trim();
                    String phonePerSt = phonePer.getText().toString().trim();
                    String pbxSt = pbx.getText().toString().trim();
                    String othersSt = others.getText().toString().trim();

                    if (TextUtils.isEmpty(nameSt)) {
                        name.setError("Please enter name!");
                        name.requestFocus();
                        return;
                    }
                    if (TextUtils.isEmpty(desgSt)) {
                        desg.setError("Please enter designation!");
                        desg.requestFocus();
                        return;
                    }
                    if (emailSt.isEmpty()) {
                        emailSt = "null";
                    }
                    if (phoneHomeSt.isEmpty()) {
                        phoneHomeSt = "null";
                    }
                    if (phonePerSt.isEmpty()) {
                        phonePerSt = "null";
                    }
                    if (pbxSt.isEmpty()) {
                        pbxSt = "null";
                    }
                    if (othersSt.isEmpty()) {
                        othersSt = "null";
                    }

                    int id = Integer.parseInt(count) + 1;
                    StringBuilder pre = new StringBuilder();
                    if (count.length() > String.valueOf(id).length()) {
                        for (int i = 0; i < (count.length() - String.valueOf(id).length()); i++) {
                            pre.append('0');
                        }
                    }
                    count = pre.append(id).toString();

                    Log.d("tag", "path: " + path + "\tcount: " + count + "/" + deptSt);
                    ChildModel model = new ChildModel("01", nameSt, desgSt, phoneHomeSt, phonePerSt, emailSt, othersSt, pbxSt);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path).child(String.valueOf(count)).child(deptSt).child("01");
                    Log.d("ref", reference.toString());
                    reference.setValue(model).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SecondaryActivity.this, SecondaryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                            startActivity(intent, options.toBundle());
                        } else {
                            Log.d("task", task.getException().getMessage());
                        }
                    });
                    dialog.dismiss();
                    alertDialog.dismiss();
                });
            }
        });
    }

    private void showContactList() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Data is retrieving, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        try {
            SharedPreferences preferences = getSharedPreferences("child", Context.MODE_PRIVATE);
            String child = preferences.getString("first_ref", null);
            databaseReference = FirebaseDatabase.getInstance().getReference("Updated").child(ROOT).child(child);
            databaseReference.keepSynced(true);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        databaseReference.child(Objects.requireNonNull(snapshot.getKey())).child(ADMIN_CHILD).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot sn : dataSnapshot.getChildren()) {
                                    for (DataSnapshot s : sn.getChildren()) {
                                        count = sn.getKey();
                                        path = dataSnapshot.getRef().getPath().toString();
                                        keyList.add(s.getKey());
                                    }
                                }
                                adapter = new AdminFinalAdapter(SecondaryActivity.this, keyList);
                                recyclerView.setAdapter(adapter);
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                ToastLong(SecondaryActivity.this, databaseError.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(SecondaryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SecondaryActivity.this, ContactNode.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}