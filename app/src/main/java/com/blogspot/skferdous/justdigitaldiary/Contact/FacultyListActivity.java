package com.blogspot.skferdous.justdigitaldiary.Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactViewAdapter;
import com.blogspot.skferdous.justdigitaldiary.Model.ChildModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.blogspot.skferdous.justdigitaldiary.Contact.DeptActivity.THIRD_CHILD;
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class FacultyListActivity extends AppCompatActivity {

    private List<ChildModel> childModelList;
    private ContactViewAdapter adapter;
    private DatabaseReference databaseReference, reference;
    public static String SECOND_CHILD = "";
    public static String FINAL_CHILD = "";
    private String backList = "";
    private TextView name, desg, email, phone, pbx, others;
    private ImageView call, mail, msg;
    private LinearLayout topLayout;
    private ListView listView;
    public List<String> arrayList = new ArrayList<>();
    public boolean role = false;

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

        SharedPreferences preferences = getSharedPreferences("child", Context.MODE_PRIVATE);
        String child = preferences.getString("second_child", null);
        String child2 = preferences.getString("final_child", null);
        backList = preferences.getString("third_child", null);
        role = preferences.getBoolean("role", false);
        SECOND_CHILD = child;
        FINAL_CHILD = child2;

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(FINAL_CHILD);

        listView = findViewById(R.id.listView);

        childModelList = new ArrayList<>();
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
        }

        return super.onOptionsItemSelected(item);
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
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot sn : dataSnapshot.getChildren()) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Updated").child(ROOT).child(child).child(snapshot.getKey()).child(THIRD_CHILD).child(sn.getKey()).child(FINAL_CHILD);
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                ChildModel model = snapshot.getValue(ChildModel.class);
                                                if (model != null) {
                                                    //Log.d("check", model.getEmail());
                                                    arrayList.add(model.getEmail());
                                                    childModelList.add(model);
                                                }
                                            }

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