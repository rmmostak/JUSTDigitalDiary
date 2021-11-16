package com.blogspot.skferdous.justdigitaldiary.Explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Model.PortalModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.UUID;

public class AddPortalActivity extends AppCompatActivity {

    private EditText title, description, link;
    private Button upload, uDelete, add;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 22;

    FirebaseStorage storage;
    public StorageReference storageReference;
    String checkAdmin = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_portal);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        title = findViewById(R.id.pTitle);
        description = findViewById(R.id.pDesc);
        link = findViewById(R.id.pLink);
        upload = findViewById(R.id.pUpload);
        add = findViewById(R.id.pAdd);
        uDelete = findViewById(R.id.uDelete);

        Intent intent = getIntent();
        checkAdmin = intent.getStringExtra("id");

        if (checkAdmin.equals("normal")) {
            uDelete.setVisibility(View.GONE);
            upload.setOnClickListener(view -> selectImage());

            add.setOnClickListener(view -> {
                String titleSt = title.getText().toString().trim();
                String descSt = description.getText().toString().trim();
                String linkSt = link.getText().toString().trim();
                if (!TextUtils.isEmpty(titleSt)) {
                    if (!TextUtils.isEmpty(descSt)) {
                        if (!TextUtils.isEmpty(linkSt)) {
                            addPortal(titleSt, descSt, linkSt);

                        } else {
                            link.setError("Please enter a valid website link.");
                            link.requestFocus();
                        }
                    } else {
                        description.setError("Please describe the link properties.");
                        description.requestFocus();
                    }
                } else {
                    title.setError("Please enter title.");
                    title.requestFocus();
                }
            });
        } else {
            String t = intent.getStringExtra("title");
            String d = intent.getStringExtra("desc");
            String l = intent.getStringExtra("link");
            String o = intent.getStringExtra("others");

            title.setText(t);
            description.setText(d);
            link.setText(l);

            upload.setOnClickListener(view -> selectImage());
            add.setText("Update");

            add.setOnClickListener(view -> {
                String titleSt = title.getText().toString().trim();
                String descSt = description.getText().toString().trim();
                String linkSt = link.getText().toString().trim();
                if (!TextUtils.isEmpty(titleSt)) {
                    if (!TextUtils.isEmpty(descSt)) {
                        if (!TextUtils.isEmpty(linkSt)) {

                            if (filePath != null) {

                                try {
                                    StorageReference reference = storageReference.child("portal/" + UUID.randomUUID().toString());
                                    reference.putFile(filePath).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {

                                        //Log.d("path", uri + "/");
                                        try {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Explore").child("Portal");

                                            PortalModel model = new PortalModel(checkAdmin, titleSt, descSt, linkSt, uri.toString());
                                            ref.child(checkAdmin).setValue(model).addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Log.d("task", Objects.requireNonNull(task.getResult()).toString());
                                                }
                                            });
                                        } catch (Exception e) {
                                            Toast.makeText(AddPortalActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }));
                                } catch (Exception e) {
                                    Log.d("filePath", e.getMessage());
                                }
                            } else {
                                updatePortal(intent.getStringExtra("id"), titleSt, descSt, linkSt, intent.getStringExtra("others"));
                            }
                        } else {
                            link.setError("Please enter a valid website link.");
                            link.requestFocus();
                        }
                    } else {
                        description.setError("Please describe the link properties.");
                        description.requestFocus();
                    }
                } else {
                    title.setError("Please enter title.");
                    title.requestFocus();
                }
            });

            uDelete.setOnClickListener(view -> deletePortal(checkAdmin, o));
        }
    }

    private void updatePortal(String id, String title, String desc, String link, String others) {

        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Explore").child("Portal");
            PortalModel model = new PortalModel(id, title, desc, link, others);
            reference.child(id).setValue(model).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(AddPortalActivity.this, PortalActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                    startActivity(intent, options.toBundle());
                }
            });
        } catch (Exception e) {
            Toast.makeText(AddPortalActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void deletePortal(String id, String others) {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Explore").child("Portal");

            reference.child(id).removeValue().addOnCompleteListener(task -> {
                Log.d("dSuccess", "Done!");
                checkAdmin = "normal";


                try {
                    StorageReference ref = storage.getReferenceFromUrl(others);
                    ref.delete().addOnSuccessListener(unused1 -> {
                        Log.d("Image Delete", unused1.toString());

                        Intent intent = new Intent(AddPortalActivity.this, PortalActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                        startActivity(intent, options.toBundle());

                    }).addOnFailureListener(e -> Log.d("Delete fail", e.getMessage()));
                } catch (Exception e) {
                    Log.d("Image Delete", e.getMessage());
                }

            }).addOnFailureListener(e ->
                    Log.d("dFail", e.getMessage()));
        } catch (Exception e) {
            Toast.makeText(AddPortalActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void addPortal(String titleSt, String desc, String linkSt) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("portal/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                    try {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Explore").child("Portal");
                                        String key = reference.push().getKey();
                                        Log.d("path", filePath + "/");
                                        PortalModel model = new PortalModel(key, titleSt, desc, linkSt, uri.toString());
                                        reference.child(Objects.requireNonNull(key)).setValue(model).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Log.d("task", uri.toString());
                                            }
                                        });
                                    } catch (Exception e) {
                                        Toast.makeText(AddPortalActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                title.setText("");
                                description.setText("");
                                link.setText("https://");
                                upload.setText("Choose image");
                                progressDialog.dismiss();
                                Toast.makeText(AddPortalActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {

                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(AddPortalActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            });
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            upload.setText(filePath.toString());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddPortalActivity.this, PortalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}