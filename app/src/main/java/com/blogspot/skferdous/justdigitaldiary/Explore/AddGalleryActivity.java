package com.blogspot.skferdous.justdigitaldiary.Explore;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.GalleryModel;
import com.blogspot.skferdous.justdigitaldiary.Model.PortalModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.UUID;

public class AddGalleryActivity extends AppCompatActivity {

    private EditText title, description;
    private Button upload, add;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 22;
    int id = 0;

    FirebaseStorage storage;
    public StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gallery);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        title = findViewById(R.id.gTitle);
        description = findViewById(R.id.gDesc);
        upload = findViewById(R.id.gUpload);
        add = findViewById(R.id.gAdd);

        upload.setOnClickListener(view -> selectImage());

        add.setOnClickListener(view -> {
            id++;
            String titleSt = title.getText().toString().trim();
            String descSt = description.getText().toString().trim();
            if (!TextUtils.isEmpty(titleSt)) {
                if (!TextUtils.isEmpty(descSt)) {
                    addGallery(String.valueOf(id), titleSt, descSt);
                } else {
                    description.setError("Please describe the link properties.");
                    description.requestFocus();
                }
            } else {
                title.setError("Please enter title.");
                title.requestFocus();
            }
        });
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

    private void addGallery(String id, String titleSt, String descSt) {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("gallery/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                    try {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Explore").child("Gallery");
                                        String key = reference.push().getKey();
                                        Log.d("path", filePath + "/");
                                        GalleryModel model = new GalleryModel(key, titleSt, uri.toString(), descSt);
                                        //PortalModel model = new PortalModel(key, titleSt, desc, linkSt, uri.toString());
                                        reference.child(Objects.requireNonNull(key)).setValue(model).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Log.d("task", uri.toString());
                                            }
                                        });
                                    } catch (Exception e) {
                                        Toast.makeText(AddGalleryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                                title.setText("");
                                description.setText("");
                                upload.setText("Choose image");
                                progressDialog.dismiss();
                                Toast.makeText(AddGalleryActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {

                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(AddGalleryActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddGalleryActivity.this, ExploreActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}