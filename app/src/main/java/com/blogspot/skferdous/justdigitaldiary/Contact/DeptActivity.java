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
import android.content.ActivityNotFoundException;
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

import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactAdapter;
import com.blogspot.skferdous.justdigitaldiary.Authentication.LoginActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.AdminModel;
import com.blogspot.skferdous.justdigitaldiary.Model.ChildModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.blogspot.skferdous.justdigitaldiary.SplashScreen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class DeptActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference databaseReference, reference;
    private List<String> keyList;
    public static String THIRD_CHILD;
    public List<String> emailList = new ArrayList<>();
    public String title = null;
    public String count = null, path = null;
    public boolean role = false;
    public FloatingActionButton addTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dept);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences preferences = getSharedPreferences("child", Context.MODE_PRIVATE);
        THIRD_CHILD = preferences.getString("third_child", null);

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
                            if (model.getId().equals(auth.getUid()) && THIRD_CHILD.equals(model.getIdentifier())) {

                                role = true;
                                return;
                            } else if (model.getId().equals(auth.getUid()) && model.getIdentifier().equals("Super Admin")) {

                                role = true;
                                return;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(DeptActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(DeptActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        //Log.d("tag", "Key " + THIRD_CHILD);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(THIRD_CHILD);

        recyclerView = findViewById(R.id.recyclerView);
        addTeacher = findViewById(R.id.addTeacher);
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
            emailIntent(emailList);
        } else if (id == R.id.editItem) {
            editItem();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void editItem() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_dept, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Add Department");
        dialogBuilder.setIcon(R.drawable.logo);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        EditText deptName = dialogView.findViewById(R.id.deptName);
        Button deptNext = dialogView.findViewById(R.id.deptNext);
        Button deptCancel = dialogView.findViewById(R.id.deptCancel);
        TextView deptHint = dialogView.findViewById(R.id.deptHint);
        deptHint.setText("Please enter department name in this pattern, 'XYZ Dept'");

        deptCancel.setOnClickListener(view1 -> alertDialog.dismiss());
        deptNext.setOnClickListener(view1 -> {
            String deptSt = deptName.getText().toString().trim();
            if (deptSt.isEmpty()) {
                deptName.setError("Please enter department name!");
                deptName.requestFocus();
                return;
            } else {
                if (deptSt.endsWith("Dept")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    LayoutInflater inflater1 = getLayoutInflater();
                    View view2 = inflater1.inflate(R.layout.child_update_layout, null);
                    builder.setView(view2);

                    builder.setTitle("Add Teacher");
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

                        //Log.d("tag", "path: " + path + "\tcount: " + count + "/" + deptSt);
                        ChildModel model = new ChildModel("01", nameSt, desgSt, phoneHomeSt, phonePerSt, emailSt, othersSt, pbxSt);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path).child(String.valueOf(count)).child(deptSt).child("01");
                        Log.d("ref", reference.toString());
                        reference.setValue(model).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(DeptActivity.this, DeptActivity.class);
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
                } else {
                    deptName.setError("Please enter department name in given pattern!");
                    deptName.requestFocus();
                }
            }
        });
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

                                                        if (model.getDesignation().toLowerCase().contains("chairman")) {
                                                            if (model.getEmail().equals("") || model.getEmail().toLowerCase().equals("null")) {
                                                                continue;
                                                            } else if (model.getEmail().contains(" ")) {
                                                                list.add(model.getEmail().replace(" ", ""));
                                                            } else {
                                                                list.add(model.getEmail());
                                                            }
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
                    Toast.makeText(DeptActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
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