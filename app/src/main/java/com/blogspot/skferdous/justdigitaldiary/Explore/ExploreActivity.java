package com.blogspot.skferdous.justdigitaldiary.Explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.MainActivity;
import com.blogspot.skferdous.justdigitaldiary.Model.GalleryModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExploreActivity extends AppCompatActivity {

    private CardView gallery, web, portal, explore;
    private DatabaseReference reference;
    private List<GalleryModel> modelList;
    private int in = 0;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        reference = FirebaseDatabase.getInstance().getReference().child("Explore").child("Gallery");
        modelList = new ArrayList<>();

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

            Intent intent=new Intent(ExploreActivity.this, CalendarWeb.class);
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

        /*explore = findViewById(R.id.explore);
        explore.setOnClickListener(v -> {
            ToastShort(this, "No more resources right now!");
        });*/

/*        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setIcon(R.drawable.logo)
                .setTitle("Notice")
                .setMessage("This feature is under constructing, please keep using to get update!")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        builder.show();*/
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
                    in = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        GalleryModel model = snapshot.getValue(GalleryModel.class);
                        GalleryModel galleryModel = new GalleryModel(model.getTopic(), model.getUrl(), model.getDetail());
                        modelList.add(galleryModel);
                    }

                    gTitle.setText(modelList.get(0).getTopic());
                    Picasso.get().load(modelList.get(0).getUrl()).into(gImage);
                    gDetail.setText(modelList.get(0).getDetail());

                    prev.setOnClickListener(v -> {
                        Toast.makeText(ExploreActivity.this, "No more resources right now!", Toast.LENGTH_SHORT).show();
                    });
                    next.setOnClickListener(v -> {
                        Toast.makeText(ExploreActivity.this, "No more resources right now!", Toast.LENGTH_SHORT).show();
                    });

                   /*
                    Toast.makeText(getApplicationContext(), String.valueOf(modelList.size()), Toast.LENGTH_LONG).show();
                    if (!modelList.isEmpty()) {
                        while (modelList.size() > in) {
                            gTitle.setText(modelList.get(0).getTopic());
                            Picasso.get().load(modelList.get(0).getUrl()).into(gImage);
                            gDetail.setText(modelList.get(0).getDetail());

                            next.setOnClickListener(v -> {
                                if (in >= modelList.size()) {
                                    return;
                                } else {
                                    gTitle.setText(modelList.get(in).getTopic());
                                    Picasso.get().load(modelList.get(in).getUrl()).into(gImage);
                                    gDetail.setText(modelList.get(in).getDetail());
                                }
                                in++;
                            });

                            prev.setOnClickListener(v -> {
                                if (in < 1) {
                                    return;
                                } else {
                                    gTitle.setText(modelList.get(in).getTopic());
                                    Picasso.get().load(modelList.get(in).getUrl()).into(gImage);
                                    gDetail.setText(modelList.get(in).getDetail());
                                }
                                in--;
                            });
                        }
                    }*/

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
            in = 1;
            return;
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
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
}