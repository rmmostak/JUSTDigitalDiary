package com.blogspot.skferdous.justdigitaldiary.Explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Adapter.PortalAdapter;
import com.blogspot.skferdous.justdigitaldiary.Model.AdminModel;
import com.blogspot.skferdous.justdigitaldiary.Model.PortalModel;
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

public class PortalActivity extends AppCompatActivity {

    private RecyclerView portalRecycler;
    private List<PortalModel> portalModelList;
    private ProgressBar progressBar;
    private FloatingActionButton addPortal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        portalRecycler = findViewById(R.id.portalRecycler);
        progressBar = findViewById(R.id.portalProgress);
        addPortal = findViewById(R.id.addPortal);

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
                            if (model.getId().equals(auth.getUid()) && model.getIdentifier().equals("all")) {

                                addPortal.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(PortalActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(PortalActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        addPortal.setOnClickListener(view -> {
            Intent intent = new Intent(PortalActivity.this, AddPortalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("id", "normal");
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PortalActivity.this);
        portalRecycler.setLayoutManager(layoutManager);
        portalRecycler.setItemAnimator(new DefaultItemAnimator());

        portalModelList = new ArrayList<>();
        showPortalList();

    }

    private void showPortalList() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Explore").child("Portal");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PortalModel model = snapshot.getValue(PortalModel.class);
                        portalModelList.add(model);
                    }
                    PortalAdapter adapter = new PortalAdapter(PortalActivity.this, portalModelList);
                    portalRecycler.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(PortalActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            });

        } catch (Exception e) {
            Toast.makeText(PortalActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PortalActivity.this, ExploreActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }
}