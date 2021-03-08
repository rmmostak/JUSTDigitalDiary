package com.blogspot.skferdous.justdigitaldiary.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.skferdous.justdigitaldiary.Model.AuthModel;
import com.blogspot.skferdous.justdigitaldiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText signupEmail, signupPass, signupConfirmPass, name, dept;
    private TextView signupWarning, signupSignIn;
    private Button signupSignUpButton;
    private LinearLayout signUpLayout;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference, studentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            CoordinatorLayout coordinatorLayout;
            coordinatorLayout = findViewById(R.id.coordinator);
            if (!isConnected()) {
                Snackbar.make(coordinatorLayout, "You don't have internet connection, Please connect!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", v -> {
                            return;
                        }).show();
            }
        }

        signupEmail = findViewById(R.id.signupEmail);
        signupPass = findViewById(R.id.signupPass);
        signupConfirmPass = findViewById(R.id.signupConfirmPass);
        name = findViewById(R.id.name);
        dept = findViewById(R.id.dept);
        signupWarning = findViewById(R.id.signupWarning);
        signupSignUpButton = findViewById(R.id.signupSignUpButton);
        signupSignIn = findViewById(R.id.signupSignIn);
        signUpLayout = findViewById(R.id.signUpLayout);

        signupSignUpButton.setOnClickListener(v -> {
            actionForSignUp();
        });

        signupSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
            startActivity(intent, options.toBundle());
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
        startActivity(intent, options.toBundle());
        super.onBackPressed();
    }

    private void actionForSignUp() {

        String mail = signupEmail.getText().toString().trim();
        String p1 = signupPass.getText().toString();
        String p2 = signupConfirmPass.getText().toString();
        String _name = name.getText().toString().trim();
        String _dept = dept.getText().toString().trim();

        String result = checkEmailValidity(mail);

        if (!TextUtils.isEmpty(mail)) {

            if (Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {

                if (result.equals("just.edu.bd") || result.equals("student.just.edu.bd")) {

                    if (!TextUtils.isEmpty(p1)) {

                        if (!TextUtils.isEmpty(p2)) {

                            if (p1.equals(p2)) {

                                if (!_name.isEmpty()) {

                                    if (!_dept.isEmpty()) {

                                        signUpforNewUser(mail, p2, _name, _dept);
                                    } else {
                                        dept.setError("Set your department name!!");
                                        dept.requestFocus();
                                        return;
                                    }

                                } else {
                                    name.setError("Set your full name!!");
                                    name.requestFocus();
                                    return;
                                }

                            } else {
                                signupConfirmPass.setError("Set the same password!!");
                                signupConfirmPass.requestFocus();
                                return;
                            }
                        } else {
                            signupConfirmPass.setError("Confirm your password!!");
                            signupConfirmPass.requestFocus();
                            return;
                        }
                    } else {
                        signupPass.setError("Enter password!!");
                        signupPass.requestFocus();
                        return;
                    }

                } else {
                    signupEmail.setError("Enter your institutional email!!");
                    signupEmail.requestFocus();
                    return;
                }

            } else {
                signupEmail.setError("Enter a valid email address!!");
                signupEmail.requestFocus();
                return;
            }
        } else {
            signupEmail.setError("Enter an email address!!");
            signupEmail.requestFocus();
            return;
        }
    }

    private void signUpforNewUser(String mail, String p2, String Name, String Dept) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Creating your account, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        try {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(mail, p2).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    String uid = firebaseAuth.getUid();
                    String user = checkEmailValidity(mail);
                    if (user.equals("just.edu.bd")) {
                        reference = FirebaseDatabase.getInstance().getReference("Users").child("Faculty and Stuff").child(uid);
                        String id = reference.push().getKey();
                        AuthModel model = new AuthModel(id, uid, mail, Name, Dept);

                        reference.child(id).setValue(model).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {

                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    firebaseUser.sendEmailVerification().addOnCompleteListener(task3 -> {
                                        if (task3.isSuccessful()) {

                                            dialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                            builder.setTitle("Alert!");
                                            builder.setIcon(R.drawable.logo);
                                            builder.setMessage("You have created account successfully, Please verify your email!!");
                                            builder.setPositiveButton("OK", (dialog1, which) -> {

                                                signupEmail.setText("");
                                                signupPass.setText("");
                                                signupConfirmPass.setText("");

                                                firebaseAuth.signOut();
                                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                                startActivity(intent, options.toBundle());
                                                finish();
                                            });
                                            builder.show();

                                        } else {
                                            dialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                            builder.setTitle("Alert!");
                                            builder.setIcon(R.drawable.logo);
                                            builder.setMessage(task3.getException().getMessage());
                                            builder.setPositiveButton("Try Again", (dialog1, which) -> {
                                                return;
                                            });
                                            builder.show();
                                        }
                                    });
                                }

                            } else {
                                dialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setTitle("Alert!");
                                builder.setIcon(R.drawable.logo);
                                builder.setMessage(task1.getException().getMessage());
                                builder.setPositiveButton("Try Again", (dialog1, which) -> {
                                    return;
                                });
                                builder.show();
                            }
                        });
                    } else if (user.equals("student.just.edu.bd")) {
                        studentReference = FirebaseDatabase.getInstance().getReference("Users").child("Students").child(uid);
                        String id = studentReference.push().getKey();
                        AuthModel model = new AuthModel(id, uid, mail, Name, Dept);
                        studentReference.child(id).setValue(model).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    firebaseUser.sendEmailVerification().addOnCompleteListener(task3 -> {
                                        if (task3.isSuccessful()) {

                                            dialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                            builder.setTitle("Alert!");
                                            builder.setIcon(R.drawable.logo);
                                            builder.setMessage("You have created account successfully, Please verify your email!!");
                                            builder.setPositiveButton("OK", (dialog1, which) -> {

                                                signupEmail.setText("");
                                                signupPass.setText("");
                                                signupConfirmPass.setText("");

                                                firebaseAuth.signOut();
                                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.fade_in, R.anim.fade_out);
                                                startActivity(intent, options.toBundle());
                                                finish();
                                            });
                                            builder.show();

                                        } else {
                                            dialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                            builder.setTitle("Alert!");
                                            builder.setIcon(R.drawable.logo);
                                            builder.setMessage(task3.getException().getMessage());
                                            builder.setPositiveButton("Try Again", (dialog1, which) -> {
                                                return;
                                            });
                                            builder.show();
                                        }
                                    });
                                }

                            } else {
                                dialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setTitle("Alert!");
                                builder.setIcon(R.drawable.logo);
                                builder.setMessage(task1.getException().getMessage());
                                builder.setPositiveButton("OK", (dialog1, which) -> {
                                    return;
                                });
                                builder.show();
                            }
                        });
                    }

                } else {
                    dialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Alert!");
                    builder.setIcon(R.drawable.logo);
                    builder.setMessage(task.getException().getMessage());
                    builder.setPositiveButton("OK", (dialog1, which) -> {
                        return;
                    });
                    builder.show();
                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static String checkEmailValidity(String email) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                for (int j = i + 1; j < email.length(); j++) {
                    builder.append(email.charAt(j));
                }
            }
        }
        return builder.toString();
    }
}