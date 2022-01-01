package com.blogspot.skferdous.justdigitaldiary;

import static com.blogspot.skferdous.justdigitaldiary.Authentication.SignupActivity.checkEmailValidity;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.AdminControlAdapter;
import com.blogspot.skferdous.justdigitaldiary.Model.AdminModel;
import com.blogspot.skferdous.justdigitaldiary.Model.AuthModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminControl extends AppCompatActivity {

    private ExpandableListView adminList;
    private FloatingActionButton addAdmin;
    private AdminControlAdapter controlAdapter;
    public List<String> uidList;
    public List<String> roleList;
    public List<String> titleList;
    public List<String> authList;
    public List<String> dataList;
    public List<String> keyList;
    public String uid = "", identifier = "", uidSt = "";
    private int lastExpandablePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_control);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        uidList = new ArrayList<>();
        roleList = new ArrayList<>();
        titleList = new ArrayList<>();
        dataList = new ArrayList<>();
        authList = new ArrayList<>();
        keyList = new ArrayList<>();

        addAdmin = findViewById(R.id.addAdmin);
        adminList = findViewById(R.id.adminList);

        addAdmin.setOnClickListener(view -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.admin_control_layout, null);
            dialogBuilder.setView(dialogView);

            dialogBuilder.setTitle("Add Admin");
            dialogBuilder.setIcon(R.drawable.logo);

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

            EditText email = dialogView.findViewById(R.id.adminEmail);
            Spinner roleSpinner = dialogView.findViewById(R.id.adminRoleSpinner);
            Button adminNext = dialogView.findViewById(R.id.adminNext);
            Button adminCancel = dialogView.findViewById(R.id.adminCancel);
            adminCancel.setOnClickListener(view1 -> alertDialog.dismiss());
            adminNext.setOnClickListener(view1 -> {
                String emailSt = email.getText().toString().trim();
                String roleSt = roleSpinner.getSelectedItem().toString().trim();

                if (!TextUtils.isEmpty(emailSt)) {
                    if (checkEmailValidity(emailSt).equals("just.edu.bd") || checkEmailValidity(emailSt).equals("student.just.edu.bd")) {

                        switch (roleSt) {
                            case "Select Role":
                                Toast.makeText(SuperAdminControl.this, "Please select Admin Role from the spinner!", Toast.LENGTH_LONG).show();
                                return;
                            case "Super Admin":
                                setAdmin(emailSt, roleSt, 1);
                                alertDialog.dismiss();
                                break;
                            case "Editor":
                                setAdmin(emailSt, roleSt, 2);
                                alertDialog.dismiss();
                                break;
                        }
                    } else {
                        email.setError("Enter user's institutional email!!");
                        email.requestFocus();
                    }
                } else {
                    email.setError("Enter user's email address!!");
                    email.requestFocus();
                }
            });
        });
        setListContent();

        adminList.setOnGroupExpandListener(groupPosition -> {
            if (lastExpandablePosition != -1 && lastExpandablePosition != groupPosition) {
                adminList.collapseGroup(lastExpandablePosition);
            }
            lastExpandablePosition = groupPosition;
        });
    }

    private void setAdmin(String email, String roleSt, int i) {
        if (i == 1) {
            updateAdmin(roleSt, email);
        } else {
            updateAdmin(roleSt, email);
        }
    }

    void updateAdmin(String roleSt, String email) {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin").child(roleSt);
            String key = reference.push().getKey();
            //String id = getUID(email);
            try {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(checkEmail(email));
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot sn : snapshot.getChildren()) {
                                AuthModel model = sn.getValue(AuthModel.class);
                                if (model != null) {
                                    if (email.equals(model.getEmail())) {
                                        uidSt = model.getUid();


                                        try {
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin").child(roleSt);
                                            reference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot sn : dataSnapshot.getChildren()) {
                                                        AdminModel model = sn.getValue(AdminModel.class);
                                                        if (model != null) {
                                                            if (uidSt.equals(model.getId())) {
                                                                Toast.makeText(SuperAdminControl.this, "Sorry, this person already member of this role!", Toast.LENGTH_LONG).show();
                                                                return;
                                                            }
                                                        }
                                                    }
                                                    AdminModel m = new AdminModel(uidSt, roleSt, key);
                                                    assert key != null;
                                                    reference.child(key).setValue(m);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Toast.makeText(SuperAdminControl.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } catch (Exception e) {
                                            Toast.makeText(SuperAdminControl.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }


                                    }
                                }
                            }
                        }
                        if (uidSt.isEmpty()) {
                            Toast.makeText(SuperAdminControl.this, "Sorry, Your requested email is not found!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        ToastLong(SuperAdminControl.this, databaseError.getMessage());
                    }
                });
            } catch (Exception e) {
                ToastLong(SuperAdminControl.this, e.getMessage());
            }

        } catch (Exception e) {
            Toast.makeText(SuperAdminControl.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String checkEmail(String email) {
        String check = "";
        if (checkEmailValidity(email).equals("just.edu.bd")) {
            check = "Faculty and Stuff";
        } else if (checkEmailValidity(email).equals("student.just.edu.bd")) {
            check = "Students";
        }
        return check;
    }

    void setListContent() {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Admin");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    roleList.clear();
                    authList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            AdminModel model = sn.getValue(AdminModel.class);
                            assert model != null;
                            uidList.add(model.getId());
                            roleList.add(model.getIdentifier());
                            keyList.add(model.getKey());
                            uid = model.getId();
                            //identifier = model.getIdentifier();

                            authList.add(getaModel(model.getId()));
                        }
                    }
                    controlAdapter = new AdminControlAdapter(SuperAdminControl.this, roleList, authList);
                    adminList.setAdapter(controlAdapter);
                    adminList.setOnItemLongClickListener((adapterView, view, i, l) -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SuperAdminControl.this);
                        builder.setTitle("Waring!");
                        builder.setIcon(R.drawable.logo);
                        builder.setMessage("Are you sure to delete this admin control?");
                        builder.setPositiveButton("Yes", (dialog1, which) -> {

                            try {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Admin").child(roleList.get(i));
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot sn : dataSnapshot.getChildren()) {
                                            AdminModel model = sn.getValue(AdminModel.class);
                                            if (model != null) {
                                                if (model.getKey().equals(keyList.get(i))) {

                                                    sn.getRef().removeValue().addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(SuperAdminControl.this, SuperAdminControl.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                                            startActivity(intent, options.toBundle());
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(SuperAdminControl.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (Exception e) {
                                Toast.makeText(SuperAdminControl.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", (dialogInterface, i1) -> {
                            return;
                        });
                        builder.show();
                        return false;
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SuperAdminControl.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(SuperAdminControl.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    String getaModel(String id) {
        String userId = "";
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            for (DataSnapshot s : sn.getChildren()) {

                                AuthModel model = s.getValue(AuthModel.class);
                                assert model != null;
                                if (model.getUid().equals(id)) {
                                    uid = s.getRef().toString() + "";
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SuperAdminControl.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(SuperAdminControl.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        userId = uid;
        return userId;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SuperAdminControl.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}