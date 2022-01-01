package com.blogspot.skferdous.justdigitaldiary.Explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Authentication.LoginActivity;
import com.blogspot.skferdous.justdigitaldiary.Authentication.UserManual;
import com.blogspot.skferdous.justdigitaldiary.Contact.DeptActivity;
import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.AdminModel;
import com.blogspot.skferdous.justdigitaldiary.Model.GalleryModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {

    private CardView gallery, web, portal;
    private DatabaseReference reference;
    private List<GalleryModel> modelList;
    private int index = 0;
    public boolean role = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        reference = FirebaseDatabase.getInstance().getReference().child("Explore").child("Gallery");
        modelList = new ArrayList<>();

        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Admin");
            FirebaseAuth auth = FirebaseAuth.getInstance();
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot sn : snapshot.getChildren()) {
                            AdminModel model = sn.getValue(AdminModel.class);

                            assert model != null;
                            if (model.getId().equals(auth.getUid()) && model.getIdentifier().equals("Super Admin")) {
                                role = true;
                                return;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ExploreActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(ExploreActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        gallery = findViewById(R.id.gallery);
        gallery.setOnClickListener(v -> {
            if (isConnected()) {
                GalleryShow();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Alert!")
                        .setIcon(R.drawable.logo)
                        .setMessage("You are offline, please connect to internet and try again!")
                        .setPositiveButton("OK", ((dialog, which) -> {
                            return;
                        }));
                builder.show();
            }
        });

        web = findViewById(R.id.web);
        web.setOnClickListener(v -> {

            Intent intent = new Intent(ExploreActivity.this, WebsiteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });

        portal = findViewById(R.id.portal);
        portal.setOnClickListener(v -> {

            Intent intent = new Intent(ExploreActivity.this, PortalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void GalleryShow() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.gallery_view, null);

        dialogBuilder.setView(dialogView);

        ProgressBar imageLoading = dialogView.findViewById(R.id.imageLoading);
        ImageView prev = dialogView.findViewById(R.id.prev);
        TextView gTitle = dialogView.findViewById(R.id.gTitle);
        ImageView gImage = dialogView.findViewById(R.id.gImage);
        TextView gDetail = dialogView.findViewById(R.id.gDetail);
        ImageView next = dialogView.findViewById(R.id.next);

        try {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    modelList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GalleryModel model = snapshot.getValue(GalleryModel.class);
                        assert model != null;
                        GalleryModel galleryModel = new GalleryModel(model.getTopic(), model.getUrl(), model.getDetail());
                        modelList.add(galleryModel);
                    }

                    int size = modelList.size();

                    Picasso.get().load(modelList.get(index).getUrl()).into(gImage);
                    gTitle.setText(modelList.get(index).getTopic());
                    gDetail.setText(modelList.get(index).getDetail());
                    //Log.d("title", modelList.get(index).getTopic() + ", " + index);
                    imageLoading.setVisibility(View.GONE);
                    prev.setOnClickListener(v -> {
                        imageLoading.setVisibility(View.VISIBLE);

                        --index;
                        if (index < 0) {
                            index = size - 1;
                        }
                        Picasso.get().load(modelList.get(index).getUrl()).into(gImage);
                        gTitle.setText(modelList.get(index).getTopic());
                        gDetail.setText(modelList.get(index).getDetail());
                        //Log.d("title", modelList.get(index).getTopic() + ", " + index);
                        imageLoading.setVisibility(View.GONE);
                    });

                    next.setOnClickListener(v -> {
                        imageLoading.setVisibility(View.VISIBLE);
                        index++;
                        if (index == size) {
                            index = 0;
                        }
                        Picasso.get().load(modelList.get(index).getUrl()).into(gImage);
                        gTitle.setText(modelList.get(index).getTopic());
                        gDetail.setText(modelList.get(index).getDetail());
                        imageLoading.setVisibility(View.GONE);
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.toException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        dialogBuilder.setPositiveButton("Close", (dialog1, which) -> {
            return;
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (role) {
            getMenuInflater().inflate(R.menu.menu_add_gallery, menu);
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.addGallery) {
            Intent intent = new Intent(ExploreActivity.this, AddGalleryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ExploreActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }

    public static void ToastShort(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.getView().setBackgroundColor(Color.parseColor("#00bfa5"));
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static void ToastLong(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.parseColor("#00bfa5"));
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = null;
        try {
            input = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(Resources.getSystem(), x);
    }
}