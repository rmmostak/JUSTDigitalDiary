package com.blogspot.skferdous.justdigitaldiary;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus;

public class MainActivity extends AppCompatActivity implements InAppUpdateManager.InAppUpdateHandler {

    private AppBarConfiguration mAppBarConfiguration;

    public static final String ROOT = "JUST Digital Diary";

    public static final String ADMIN_TAG = "Administrative Offices";
    public static final String FACULTY_TAG = "Faculty Members";

    private FirebaseAuth firebaseAuth;
    private InAppUpdateManager inAppUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_about_us, R.id.nav_vc_message)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        inAppUpdateManager = InAppUpdateManager.Builder(this, 101)
                .resumeUpdates(true)
                .mode(Constants.UpdateMode.FLEXIBLE)
                .snackBarMessage("An update has been downloaded.")
                .snackBarAction("Update")
                .handler(this);

        inAppUpdateManager.checkForAppUpdate();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.signOut) {

            firebaseAuth.signOut();
            Intent intent = new Intent(MainActivity.this, Authentication.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options=ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left2right, R.anim.right2left);
            startActivity(intent, options.toBundle());
        }
            return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onInAppUpdateError(int code, Throwable error) {

    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {
        if (status.isDownloaded()) {
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(rootView,
                    "An update has been downloaded.",
                    Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("Update", v -> {
                inAppUpdateManager.completeUpdate();
            });
        }
    }
}