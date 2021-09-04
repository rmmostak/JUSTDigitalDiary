package com.blogspot.skferdous.justdigitaldiary.Contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.ContactAdapter;
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
import static com.blogspot.skferdous.justdigitaldiary.Explore.ExploreActivity.ToastLong;
import static com.blogspot.skferdous.justdigitaldiary.MainActivity.ROOT;

public class FacultyListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ChildModel> childModelList;
    private ContactViewAdapter adapter;
    private DatabaseReference databaseReference;
    public static String SECOND_CHILD = "";
    public static String FINAL_CHILD = "";
    private TextView name, desg, email, phone, pbx, others;
    private ImageView call, mail, msg;
    private LinearLayout topLayout;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_list);

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
        SECOND_CHILD = child;
        FINAL_CHILD = child2;

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(FINAL_CHILD);

        listView = findViewById(R.id.listView);

        childModelList = new ArrayList<>();
        showContactList();
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
            databaseReference = FirebaseDatabase.getInstance().getReference(ROOT).child(child);
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
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(ROOT).child(child).child(snapshot.getKey()).child(THIRD_CHILD).child(sn.getKey()).child(FINAL_CHILD);
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                ChildModel model = snapshot.getValue(ChildModel.class);
                                                childModelList.add(model);
                                            }

                                            adapter = new ContactViewAdapter(FacultyListActivity.this, childModelList);
                                            listView.setAdapter(adapter);

                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                                                }
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
}