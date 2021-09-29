package com.blogspot.skferdous.justdigitaldiary.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.blogspot.skferdous.justdigitaldiary.NotePad.InvitedNotePad;
import com.blogspot.skferdous.justdigitaldiary.NotePad.NotePad;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.blogspot.skferdous.justdigitaldiary.SplashScreen;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.blogspot.skferdous.justdigitaldiary.Authentication.SignupActivity.checkEmailValidity;

public class LoginActivity extends AppCompatActivity {

    private EditText email, pass;
    private TextView warning, signUp, forgotPass;
    private Button signIn, guest;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle("Login");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        CoordinatorLayout coordinatorLayout;
        coordinatorLayout = findViewById(R.id.coordinator);
        if (!isConnected()) {
            Snackbar.make(coordinatorLayout, "You don't have internet connection, Please connect!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", v -> {
                    }).show();
        }

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        warning = findViewById(R.id.warning);
        signUp = findViewById(R.id.signUp);
        signIn = findViewById(R.id.signIn);
        //guest = findViewById(R.id.guest);
        forgotPass = findViewById(R.id.forgotPass);

        forgotPass.setOnClickListener(v -> {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.forgot_password_layout, null);

            dialogBuilder.setView(dialogView);

            final EditText forgotMail = dialogView.findViewById(R.id.forgotMail);
            final Button verifyMail = dialogView.findViewById(R.id.verifyMail);

            dialogBuilder.setIcon(R.drawable.logo);
            dialogBuilder.setTitle("Set your new password");
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

            verifyMail.setOnClickListener(v1 -> {
                String mail = forgotMail.getText().toString().trim();
                String check = checkEmailValidity(mail);
                if (check.equals("just.edu.bd") || check.equals("student.just.edu.bd")) {
                    sendResetPassLink(mail);
                    alertDialog.dismiss();
                } else {
                    forgotMail.setError("Enter your institutional email!!");
                    forgotMail.requestFocus();
                }
            });
        });

        signIn.setOnClickListener(v -> {
            actionForSignIn();
        });

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (firebaseAuth.getCurrentUser() != null && firebaseUser.isEmailVerified()) {
                Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendResetPassLink(String mail) {

        try {
            firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Alert!");
                    builder.setIcon(R.drawable.logo);
                    builder.setMessage("We have sent you a link to reset your password, please check your email!!");
                    builder.setPositiveButton("OK", (dialog1, which) -> {
                        firebaseAuth.signOut();
                    });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Alert!");
                    builder.setIcon(R.drawable.logo);
                    builder.setMessage(Objects.requireNonNull(task.getException()).getMessage());
                    builder.setPositiveButton("OK", (dialog1, which) -> {
                    });
                }
            });

        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void actionForSignIn() {

        signIn.setOnClickListener(v -> {
            String _mail = email.getText().toString().trim();
            String _pass = pass.getText().toString().trim();
            String user = checkEmailValidity(_mail);

            if (!TextUtils.isEmpty(_mail)) {

                if (user.equals("just.edu.bd") || user.equals("student.just.edu.bd")) {

                    if (!TextUtils.isEmpty(_pass)) {

                        if (_mail.equals("mr.akhond@just.edu.bd") || _mail.equals("160134.cse@student.just.edu.bd") || _mail.equals("170144.cse@student.just.edu.bd") || _mail.equals("160101.cse@student.just.edu.bd")) {
                            SharedPreferences preferences = getSharedPreferences("driver", MODE_PRIVATE);
                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("vehicle", true);
                            editor.apply();
                        } else {
                            SharedPreferences preferences = getSharedPreferences("driver", MODE_PRIVATE);
                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("vehicle", false);
                            editor.apply();
                        }

                        getDataForExistUser(_mail, _pass);

                    } else {
                        pass.setError("Enter your password!!");
                        pass.requestFocus();
                    }
                } else {
                    email.setError("Enter your institutional email!!");
                    email.requestFocus();
                }
            } else {
                email.setError("Enter your email address!!");
                email.requestFocus();
            }
        });

    }

    private void getDataForExistUser(String mail, String p1) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Logging in, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        try {
            firebaseAuth.signInWithEmailAndPassword(mail, p1).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert firebaseUser != null;
                    boolean userStatus = firebaseUser.isEmailVerified();
                    if (userStatus) {
                        email.setText("");
                        pass.setText("");
                        dialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                        startActivity(intent, options.toBundle());
                    } else {
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Alert!");
                        builder.setIcon(R.drawable.logo);
                        builder.setMessage("Please verify your email address and try again to login!!");
                        builder.setPositiveButton("Close", (dialog1, which) -> {
                        });
                        builder.show();
                    }
                } else {
                    dialog.dismiss();
                    warning.setText(Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
        firebaseAuth.signOut();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_docs, menu);
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.docs) {
            Intent intent = new Intent(LoginActivity.this, UserManual.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        }
        return super.onOptionsItemSelected(item);
    }
}