package com.blogspot.skferdous.justdigitaldiary.Contact;

import static com.blogspot.skferdous.justdigitaldiary.Contact.DeptActivity.THIRD_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactViewAdapter;
import com.blogspot.skferdous.justdigitaldiary.Model.AdminModel;
import com.blogspot.skferdous.justdigitaldiary.Model.ChildModel;
import com.blogspot.skferdous.justdigitaldiary.R;
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

public class FacultyListActivity extends AppCompatActivity {

    private List<ChildModel> childModelList;
    private ContactViewAdapter adapter;
    private DatabaseReference databaseReference;
    public static String SECOND_CHILD = "";
    public static String FINAL_CHILD = "";
    private String backList = "";
    private TextView name, desg, email, phone, pbx, others;
    private ImageView call, mail, msg;
    private LinearLayout topLayout;
    public ListView listView;
    public List<String> arrayList = new ArrayList<>();
    public boolean role = false;
    public String id = null, path = null, count = "", title = null, identifier = null;
    private FloatingActionButton addTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_list);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        name = findViewById(R.id.name);
        desg = findViewById(R.id.designation);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        pbx = findViewById(R.id.pbx);
        others = findViewById(R.id.others);

        call = findViewById(R.id.call);
        mail = findViewById(R.id.mail);
        msg = findViewById(R.id.msg);

        topLayout = findViewById(R.id.topLayout);
        addTeacher = findViewById(R.id.addTeacher);

        SharedPreferences preferences = getSharedPreferences("child", Context.MODE_PRIVATE);
        String child = preferences.getString("second_child", null);
        String child2 = preferences.getString("final_child", null);
        backList = preferences.getString("third_child", null);

        SECOND_CHILD = child;
        FINAL_CHILD = child2;

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(FINAL_CHILD);

        //Log.d("child", THIRD_CHILD + "/" + FINAL_CHILD);
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
                    Toast.makeText(FacultyListActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(FacultyListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        listView = findViewById(R.id.listView);

        childModelList = new ArrayList<>();
        showContactList();

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Toast.makeText(FacultyListActivity.this, "Please click the edit button and try again.", Toast.LENGTH_LONG).show();
            return false;
        });
        addTeacher.setOnClickListener(view -> Toast.makeText(FacultyListActivity.this, "Please click the edit button and try again.", Toast.LENGTH_LONG).show());
    }

    private void updateInformation(ChildModel model, String i) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.child_update_layout, null);
        dialogBuilder.setView(dialogView);

        EditText name = dialogView.findViewById(R.id.name);
        EditText desg = dialogView.findViewById(R.id.designation);
        EditText email = dialogView.findViewById(R.id.email);
        EditText phoneHome = dialogView.findViewById(R.id.phoneHome);
        EditText phonePer = dialogView.findViewById(R.id.phonePer);
        EditText pbx = dialogView.findViewById(R.id.pbx);
        EditText others = dialogView.findViewById(R.id.others);
        Button update = dialogView.findViewById(R.id.update);
        Button delete = dialogView.findViewById(R.id.delete);
        Button cancel = dialogView.findViewById(R.id.cancel);

        name.setText(model.getName());
        desg.setText(model.getDesignation());
        email.setText(model.getEmail());
        phoneHome.setText(model.getPhoneHome());
        phonePer.setText(model.getPhonePer());
        pbx.setText(model.getPbx());
        others.setText(model.getOthers());

        dialogBuilder.setTitle("Update Information");
        dialogBuilder.setIcon(R.drawable.logo);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(FacultyListActivity.this);
            builder.setTitle("Alert!");
            builder.setIcon(R.drawable.logo);
            builder.setMessage("Are you sure to delete this?");
            builder.setPositiveButton("Yes", (dialog1, which) -> {
                try {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path).child(i);
                    reference.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(FacultyListActivity.this, FacultyListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                            startActivity(intent, options.toBundle());
                        } else {
                            Log.d("task", task.getException().getMessage());
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(FacultyListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                dialog1.dismiss();
            });
            builder.setNegativeButton("Cancel", (dialogInterface, i1) -> dialogInterface.dismiss());
            builder.show();

        });
        cancel.setOnClickListener(view -> alertDialog.dismiss());
        update.setOnClickListener(view -> {
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

            ChildModel updateModel = new ChildModel(model.getId(), nameSt, desgSt, phoneHomeSt, phonePerSt, emailSt, othersSt, pbxSt);
            setUpdate(updateModel, model.getId());
            alertDialog.dismiss();
        });
    }

    private void setUpdate(ChildModel model, String id) {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path).child(id);
            reference.setValue(model).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(FacultyListActivity.this, FacultyListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent, options.toBundle());
                } else {
                    Log.d("task", Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        } catch (Exception e) {
            Toast.makeText(FacultyListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
            emailIntent(arrayList);
        } else if (id == R.id.editItem) {
            editItem();
        }

        return super.onOptionsItemSelected(item);
    }

    private void editItem() {
        addTeacher.setVisibility(View.VISIBLE);
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            ChildModel model = childModelList.get(i);
            updateInformation(model, model.getId());

            return false;
        });

        addTeacher.setOnClickListener(view -> {
            try {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.child_update_layout, null);
                dialogBuilder.setView(dialogView);

                EditText name = dialogView.findViewById(R.id.name);
                EditText desg = dialogView.findViewById(R.id.designation);
                EditText email = dialogView.findViewById(R.id.email);
                EditText phoneHome = dialogView.findViewById(R.id.phoneHome);
                EditText phonePer = dialogView.findViewById(R.id.phonePer);
                EditText pbx = dialogView.findViewById(R.id.pbx);
                EditText others = dialogView.findViewById(R.id.others);
                Button update = dialogView.findViewById(R.id.update);
                Button delete = dialogView.findViewById(R.id.delete);
                Button cancel = dialogView.findViewById(R.id.cancel);

                delete.setVisibility(View.GONE);
                dialogBuilder.setTitle("Add Person");
                dialogBuilder.setIcon(R.drawable.logo);

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                cancel.setOnClickListener(v -> alertDialog.dismiss());
                update.setOnClickListener(v -> {
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

                    ChildModel model = new ChildModel(count, nameSt, desgSt, phoneHomeSt, phonePerSt, emailSt, othersSt, pbxSt);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path).child(String.valueOf(count));
                    reference.setValue(model).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(FacultyListActivity.this, FacultyListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                            startActivity(intent, options.toBundle());
                        } else {
                            Log.d("task", task.getException().getMessage());
                        }
                    });
                });
            } catch (Exception e) {
                Toast.makeText(FacultyListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void emailIntent(List<String> eList) {
        String sendTo = eList.toString().substring(1, eList.toString().length() - 1);
        //Log.d("email", sendTo);
        if (sendTo.isEmpty()) {
            Toast.makeText(FacultyListActivity.this, "Sorry, email address is not found", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{sendTo});
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(Intent.createChooser(intent, "Send via..."));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(FacultyListActivity.this, "Sorry, email address is not found!", Toast.LENGTH_SHORT).show();
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
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Updated").child(ROOT).child(child).child(snapshot.getKey()).child(THIRD_CHILD).child(sn.getKey()).child(FINAL_CHILD);
                                    //path = reference.getPath().toString();
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                path = dataSnapshot.getRef().getPath().toString();
                                                count = snapshot.getKey();

                                                ChildModel model = snapshot.getValue(ChildModel.class);
                                                if (model != null) {
                                                    //Log.d("check", model.getEmail());
                                                    arrayList.add(model.getEmail());
                                                    childModelList.add(model);
                                                }
                                            }
                                            /*Log.d("child", childModelList.size() + "");
                                            if (childModelList.size() < 1) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(FacultyListActivity.this);
                                                builder.setTitle("Alert!");
                                                builder.setIcon(R.drawable.logo);
                                                builder.setMessage("No contents are available here, back to home.");
                                                builder.setPositiveButton("Ok", (dialog1, which) -> {

                                                    Intent intent = new Intent(FacultyListActivity.this, ContactNode.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                                    startActivity(intent, options.toBundle());

                                                    dialog1.dismiss();
                                                });
                                                builder.show();
                                            }*/
                                            adapter = new ContactViewAdapter(FacultyListActivity.this, childModelList);
                                            listView.setAdapter(adapter);

                                            listView.setOnItemClickListener((parent, view, position, id) -> {
                                                ChildModel model = childModelList.get(position);
                                                Animation animation = AnimationUtils.loadAnimation(FacultyListActivity.this, R.anim.fade_in);
                                                topLayout.setVisibility(View.VISIBLE);
                                                topLayout.setAnimation(animation);
                                                name.setText(model.getName());
                                                desg.setText(model.getDesignation());
                                                if (model.getEmail().toLowerCase().equals("null")) {
                                                    email.setVisibility(View.GONE);
                                                } else {
                                                    email.setVisibility(View.VISIBLE);
                                                    email.setText(model.getEmail());
                                                }
                                                if (model.getPhonePer().toLowerCase().equals("null") || model.getPhonePer().equals("")) {
                                                    phone.setText(model.getPhoneHome());
                                                } else {
                                                    phone.setText(model.getPhoneHome() + ", " + model.getPhonePer());
                                                }
                                                if (model.getPhoneHome().toLowerCase().equals("null") || model.getPhoneHome().equals("")) {
                                                    phone.setVisibility(View.GONE);
                                                } else {
                                                    phone.setVisibility(View.VISIBLE);
                                                    phone.setText(model.getPhoneHome());
                                                }
                                                if (model.getPbx().toLowerCase().equals("null") || model.getPbx().equals("")) {
                                                    pbx.setVisibility(View.GONE);
                                                } else {
                                                    pbx.setVisibility(View.VISIBLE);
                                                    pbx.setText(model.getPbx());
                                                }
                                                if (model.getOthers().toLowerCase().equals("null") || model.getOthers().equals("")) {
                                                    others.setVisibility(View.GONE);
                                                } else {
                                                    others.setVisibility(View.VISIBLE);
                                                    others.setText(model.getOthers());
                                                }

                                                msg.setOnClickListener(v -> {
                                                    if (model.getPhoneHome().isEmpty() || model.getPhoneHome().toLowerCase().equals("null")) {
                                                        Toast.makeText(FacultyListActivity.this, "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeMessage(model.getPhoneHome());
                                                    }
                                                });

                                                call.setOnClickListener(v -> {
                                                    if (model.getPhoneHome().isEmpty() || model.getPhoneHome().toLowerCase().equals("null")) {
                                                        Toast.makeText(FacultyListActivity.this, "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeCall(model.getPhoneHome());
                                                    }
                                                });

                                                mail.setOnClickListener(v -> {
                                                    if (model.getEmail().isEmpty() || model.getEmail().toLowerCase().equals("null")) {
                                                        Toast.makeText(FacultyListActivity.this, "Sorry, Email address is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeMail(model.getEmail());
                                                    }
                                                });
                                            });

                                            if (childModelList.size() > 0) {
                                                ChildModel model = childModelList.get(0);
                                                Animation animation = AnimationUtils.loadAnimation(FacultyListActivity.this, R.anim.fade_in);
                                                topLayout.setVisibility(View.VISIBLE);
                                                topLayout.setAnimation(animation);
                                                name.setText(model.getName());
                                                desg.setText(model.getDesignation());
                                                if (model.getEmail().toLowerCase().equals("null")) {
                                                    email.setVisibility(View.GONE);
                                                } else {
                                                    email.setText(model.getEmail());
                                                }
                                                if (model.getPhonePer().toLowerCase().equals("null") || model.getPhonePer().equals("")) {
                                                    phone.setText(model.getPhoneHome());
                                                } else {
                                                    phone.setText(model.getPhoneHome() + ", " + model.getPhonePer());
                                                }
                                                if (model.getPhoneHome().toLowerCase().equals("null") || model.getPhoneHome().equals("")) {
                                                    phone.setVisibility(View.GONE);
                                                } else {
                                                    phone.setText(model.getPhoneHome());
                                                }
                                                if (model.getPbx().toLowerCase().equals("null") || model.getPbx().equals("")) {
                                                    pbx.setVisibility(View.GONE);
                                                } else {
                                                    pbx.setText(model.getPbx());
                                                }
                                                if (model.getOthers().toLowerCase().equals("null") || model.getOthers().equals("")) {
                                                    others.setVisibility(View.GONE);
                                                } else {
                                                    others.setText(model.getOthers());
                                                }

                                                msg.setOnClickListener(v -> {
                                                    if (model.getPhoneHome().isEmpty() || model.getPhoneHome().toLowerCase().equals("null")) {
                                                        Toast.makeText(FacultyListActivity.this, "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeMessage(model.getPhoneHome());
                                                    }
                                                });

                                                call.setOnClickListener(v -> {
                                                    if (model.getPhoneHome().isEmpty() || model.getPhoneHome().toLowerCase().equals("null")) {
                                                        Toast.makeText(FacultyListActivity.this, "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeCall(model.getPhoneHome());
                                                    }
                                                });

                                                mail.setOnClickListener(v -> {
                                                    if (model.getEmail().isEmpty() || model.getEmail().toLowerCase().equals("null")) {
                                                        Toast.makeText(FacultyListActivity.this, "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeMail(model.getEmail());
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            ToastLong(FacultyListActivity.this, databaseError.getMessage());
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                ToastLong(FacultyListActivity.this, databaseError.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ToastLong(FacultyListActivity.this, databaseError.getMessage());
                }
            });
            dialog.dismiss();

        } catch (Exception e) {
            Toast.makeText(FacultyListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void makeCall(String number) {
        if (number.isEmpty()) {
            Toast.makeText(FacultyListActivity.this, "Sorry, This field is empty!", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (FacultyListActivity.this.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(context, "Unissued call permission!", Toast.LENGTH_LONG).show();
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 10);
                    return;
                }
                ActivityOptions options = ActivityOptions.makeCustomAnimation(FacultyListActivity.this, R.anim.fade_in, R.anim.fade_out);
                startActivity(intent, options.toBundle());
            }
        }

    }

    private void makeMessage(String number) {
        String MSG = "Hello, ";
        if (number.isEmpty() && MSG.isEmpty()) {
            Toast.makeText(FacultyListActivity.this, "Sorry, we couldn't find any number to send message!", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("smsto:" + number));
            intent.putExtra("sms_body", MSG);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            try {
                startActivity(Intent.createChooser(intent, "Choose a message client... "));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(FacultyListActivity.this, "Sorry, messaging address is not found!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void makeMail(String email) {
        String sendTo = email;
        String sendSub = "Subject";
        String sendBody = "Message";
        if (sendTo.isEmpty() && sendSub.isEmpty() && sendBody.isEmpty()) {
            Toast.makeText(FacultyListActivity.this, "Sorry, email address is not found", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{sendTo});
            intent.putExtra(Intent.EXTRA_SUBJECT, sendSub);
            intent.putExtra(Intent.EXTRA_TEXT, sendBody);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(Intent.createChooser(intent, "Choose an email client..."));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(FacultyListActivity.this, "Sorry, email address is not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FacultyListActivity.this, DeptActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("listBack", backList);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}