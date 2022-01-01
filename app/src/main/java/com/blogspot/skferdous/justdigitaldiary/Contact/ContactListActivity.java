package com.blogspot.skferdous.justdigitaldiary.Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.blogspot.skferdous.justdigitaldiary.Contact.FacultyListActivity.SECOND_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class ContactListActivity extends AppCompatActivity {

    //private RecyclerView recyclerView;
    private ListView listView;
    private List<ChildModel> childModelList;
    private ContactViewAdapter adapter;
    private DatabaseReference reference;
    public static String FINAL_CHILD = "";
    private TextView name, desg, email, phone, pbx, others;
    private ImageView call, mail, msg;
    private LinearLayout topLayout;
    public boolean role = false;
    public List<String> arrayList = new ArrayList<>();
    public String id = null, path = null, count = "", title = null, identifier = null;
    private FloatingActionButton addTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
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
        String child2 = preferences.getString("last_child", null);
        role = preferences.getBoolean("role", false);
        SECOND_CHILD = child;
        FINAL_CHILD = child2;

        ActionBar bar = getSupportActionBar();
        bar.setTitle(FINAL_CHILD);

        listView = findViewById(R.id.listView);
        childModelList = new ArrayList<>();

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
                                if (model.getIdentifier().equals("Super Admin") || model.getIdentifier().equals("Editor")) {
                                    role = true;
                                    return;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ContactListActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(ContactListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        showContactList();
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
                android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
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
                            Intent intent = new Intent(ContactListActivity.this, ContactListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                            startActivity(intent, options.toBundle());
                        } else {
                            Log.d("task", Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
                });
            } catch (Exception e) {
                Toast.makeText(ContactListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void emailIntent(List<String> eList) {
        String sendTo = eList.toString().substring(1, eList.toString().length() - 1);
        //Log.d("email", sendTo);
        if (sendTo.isEmpty()) {
            Toast.makeText(ContactListActivity.this, "Sorry, email address is not found", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{sendTo});
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(Intent.createChooser(intent, "Send via..."));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ContactListActivity.this, "Sorry, email address is not found!", Toast.LENGTH_SHORT).show();
            }
        }
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
            //Log.d("path", path + "/" + i);
            AlertDialog.Builder builder = new AlertDialog.Builder(ContactListActivity.this);
            builder.setTitle("Alert!");
            builder.setIcon(R.drawable.logo);
            builder.setMessage("Are you sure to delete this?");
            builder.setPositiveButton("Yes", (dialog1, which) -> {
                try {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(path).child(i);
                    reference.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(ContactListActivity.this, ContactListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                            startActivity(intent, options.toBundle());
                        } else {
                            Log.d("task", task.getException().getMessage());
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(ContactListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
                    Intent intent = new Intent(ContactListActivity.this, ContactListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent, options.toBundle());
                } else {
                    Log.d("task", Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        } catch (Exception e) {
            Toast.makeText(ContactListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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
            reference = FirebaseDatabase.getInstance().getReference().child("Updated").child(ROOT).child(child);
            //ToastShort(this, THIRD_CHILD);
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        reference.child(Objects.requireNonNull(snapshot.getKey())).child(SECOND_CHILD).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot sn : dataSnapshot.getChildren()) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Updated").child(ROOT).child(child).child(snapshot.getKey()).child(SECOND_CHILD).child(sn.getKey()).child(FINAL_CHILD);
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @SuppressLint({"RestrictedApi", "SetTextI18n"})
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot ss : dataSnapshot.getChildren()) {
                                                path = dataSnapshot.getRef().getPath().toString();
                                                count = ss.getKey();

                                                ChildModel model = ss.getValue(ChildModel.class);

                                                if (model != null) {
                                                    arrayList.add(model.getEmail());
                                                    childModelList.add(model);
                                                }
                                            }

                                            adapter = new ContactViewAdapter(ContactListActivity.this, childModelList);
                                            listView.setAdapter(adapter);

                                            listView.setOnItemClickListener((parent, view, position, id) -> {
                                                ChildModel model = childModelList.get(position);
                                                Animation animation = AnimationUtils.loadAnimation(ContactListActivity.this, R.anim.fade_in);
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
                                                        Toast.makeText(ContactListActivity.this, "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeMessage(model.getPhoneHome());
                                                    }
                                                });

                                                call.setOnClickListener(v -> {
                                                    if (model.getPhoneHome().isEmpty() || model.getPhoneHome().toLowerCase().equals("null")) {
                                                        Toast.makeText(ContactListActivity.this, "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeCall(model.getPhoneHome());
                                                    }
                                                });

                                                mail.setOnClickListener(v -> {
                                                    if (model.getEmail().isEmpty() || model.getEmail().toLowerCase().equals("null")) {
                                                        Toast.makeText(ContactListActivity.this, "Sorry, Email address is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeMail(model.getEmail());
                                                    }
                                                });
                                            });

                                            if (childModelList.size() > 0) {
                                                ChildModel model = childModelList.get(0);
                                                Animation animation = AnimationUtils.loadAnimation(ContactListActivity.this, R.anim.fade_in);
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
                                                        Toast.makeText(ContactListActivity.this, "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeMessage(model.getPhoneHome());
                                                    }
                                                });

                                                call.setOnClickListener(v -> {
                                                    if (model.getPhoneHome().isEmpty() || model.getPhoneHome().toLowerCase().equals("null")) {
                                                        Toast.makeText(ContactListActivity.this, "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeCall(model.getPhoneHome());
                                                    }
                                                });

                                                mail.setOnClickListener(v -> {
                                                    if (model.getEmail().isEmpty() || model.getEmail().toLowerCase().equals("null")) {
                                                        Toast.makeText(ContactListActivity.this, "Sorry, Phone number is empty!", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        makeMail(model.getEmail());
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            ToastLong(ContactListActivity.this, databaseError.getMessage());
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                ToastLong(ContactListActivity.this, databaseError.getMessage());
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
            Toast.makeText(ContactListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void makeCall(String number) {
        if (number.isEmpty()) {
            Toast.makeText(ContactListActivity.this, "Sorry, This field is empty!", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContactListActivity.this.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(context, "Unissued call permission!", Toast.LENGTH_LONG).show();
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 10);
                    return;
                }
                ActivityOptions options = ActivityOptions.makeCustomAnimation(ContactListActivity.this, R.anim.fade_in, R.anim.fade_out);
                startActivity(intent, options.toBundle());
            }
        }

    }

    private void makeMessage(String number) {
        String MSG = "Hello, ";
        if (number.isEmpty() && MSG.isEmpty()) {
            Toast.makeText(ContactListActivity.this, "Sorry, we couldn't find any number to send message!", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("smsto:" + number));
            intent.putExtra("sms_body", MSG);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            try {
                startActivity(Intent.createChooser(intent, "Choose a message client... "));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(ContactListActivity.this, "Sorry, messaging address is not found!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void makeMail(String email) {
        String sendTo = email;
        String sendSub = "Subject";
        String sendBody = "Message";
        if (sendTo.isEmpty() && sendSub.isEmpty() && sendBody.isEmpty()) {
            Toast.makeText(ContactListActivity.this, "Sorry, email address is not found", Toast.LENGTH_LONG).show();
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
                Toast.makeText(ContactListActivity.this, "Sorry, email address is not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ContactListActivity.this, SecondaryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}