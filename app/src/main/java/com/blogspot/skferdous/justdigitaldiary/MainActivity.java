package com.blogspot.skferdous.justdigitaldiary;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Authentication.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.blogspot.skferdous.justdigitaldiary.Authentication.SignupActivity.checkEmailValidity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    public static final String ROOT = "JUST Digital Diary";
    public static final String NOTE_ROOT = "JUST Digital Diary Notes";

    public static final String ADMIN_TAG = "Administrative Offices";
    public static final String FACULTY_TAG = "Faculty Members";

    private FirebaseAuth firebaseAuth;
    private TextView navUserTitle, navUserDept;
    private ImageButton logOut;

    //in app update
    private int MY_REQUEST_CODE = 111;
    private AppUpdateManager mAppUpdateManager;
    private int RC_APP_UPDATE = 999;
    private int inAppUpdateType;
    private com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask;
    private InstallStateUpdatedListener installStateUpdatedListener;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;

    private String uName = "", uDept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navUserTitle = findViewById(R.id.navUserTitle);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*start of in app update functions*/

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(result -> {
            if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            result,
                            AppUpdateType.IMMEDIATE,
                            MainActivity.this,
                            MY_REQUEST_CODE
                    );
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        /*end of in app update function*/

        firebaseAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about_us, R.id.nav_vc_message, R.id.nav_feedBack, R.id.signOut)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = getLayoutInflater().inflate(R.layout.nav_header_main, navigationView, false);
        navigationView.addHeaderView(headerView);

        /* TODO get the IMAGE and make it clickable */

        navUserTitle = headerView.findViewById(R.id.navUserTitle);
        navUserDept = headerView.findViewById(R.id.navUserDept);
        logOut=headerView.findViewById(R.id.signOut);
        logOut.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Alert!!");
            builder.setIcon(R.drawable.logo);
            builder.setMessage("Are you sure to sign out?");
            builder.setPositiveButton("Yes", (dialog1, which) -> {

                firebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left2right, R.anim.right2left);
                startActivity(intent, options.toBundle());

                dialog1.dismiss();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.show();

        });

        setNameWithDept();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            Log.d("status", "Download started");
            popupSnackBarForCompleteUpdate();
            if (resultCode != RESULT_OK) {
                Log.d("result status", "download failed");
            }
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        try {
            Snackbar snackbar =
                    Snackbar.make(
                            findViewById(R.id.coordinator),
                            "An update has just been downloaded.\nRestart to update",
                            Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("INSTALL", v -> {
                if (mAppUpdateManager != null) {
                    mAppUpdateManager.completeUpdate();
                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimaryDark));
            snackbar.show();

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        super.onBackPressed();
    }

    public void setNameWithDept() {

        try {
            DatabaseReference reference;
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String uid = auth.getCurrentUser().getUid();
            String check = checkEmailValidity(auth.getCurrentUser().getEmail());
            if (check.equals("just.edu.bd")) {
                reference = FirebaseDatabase.getInstance().getReference("Users").child("Faculty and Stuff").child(uid);
            } else {
                reference = FirebaseDatabase.getInstance().getReference("Users").child("Students").child(uid);
            }

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        uName = snapshot.child("name").getValue(String.class);
                        uDept = snapshot.child("dept").getValue(String.class);
                        int orientation = MainActivity.this.getResources().getConfiguration().orientation; //.getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            if (uName.length() > 25) {
                                StringBuilder builder = new StringBuilder();
                                for (int i = 0; i < 23; i++) {
                                    builder.append(uName.charAt(i));
                                }
                                navUserTitle.setText(builder + "...");
                            } else {
                                navUserTitle.setText(uName);
                            }
                        } else {
                            navUserTitle.setText(uName);
                        }

                        navUserDept.setText(uDept);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}