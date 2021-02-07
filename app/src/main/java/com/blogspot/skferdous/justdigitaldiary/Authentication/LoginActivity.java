package com.blogspot.skferdous.justdigitaldiary.Authentication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.blogspot.skferdous.justdigitaldiary.R;
import com.blogspot.skferdous.justdigitaldiary.SplashScreen;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.blogspot.skferdous.justdigitaldiary.Authentication.SignupActivity.checkEmailValidity;

public class LoginActivity extends AppCompatActivity {

    private EditText email, pass;
    private TextView warning, signUp, forgotPass;
    private Button signIn;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    //private SignInButton googleSignInUp;

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "DIGITALDIARYJUST";
    //GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar bar=getSupportActionBar();
        bar.setTitle("Login");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
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
                        return;
                    }).show();
        }

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        warning = findViewById(R.id.warning);
        signUp = findViewById(R.id.signUp);
        signIn = findViewById(R.id.signIn);
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
                    return;
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
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left2right, R.anim.right2left);
            startActivity(intent, options.toBundle());
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (firebaseAuth.getCurrentUser() != null) {
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
                        return;
                    });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Alert!");
                    builder.setIcon(R.drawable.logo);
                    builder.setMessage(task.getException().getMessage());
                    builder.setPositiveButton("OK", (dialog1, which) -> {
                        return;
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

                        getDataForExistUser(_mail, _pass);

                    } else {
                        pass.setError("Enter your password!!");
                        pass.requestFocus();
                        return;
                    }
                } else {
                    email.setError("Enter your institutional email!!");
                    email.requestFocus();
                    return;
                }
            } else {
                email.setError("Enter your email address!!");
                email.requestFocus();
                return;
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
                        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left2right, R.anim.right2left);
                        startActivity(intent, options.toBundle());
                    } else {
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Alert!");
                        builder.setIcon(R.drawable.logo);
                        builder.setMessage("Please verify your email address and try again to login!!");
                        builder.setPositiveButton("Close", (dialog1, which) -> {
                            return;
                        });
                        builder.show();
                    }
                } else {
                    dialog.dismiss();
                    warning.setText(task.getException().getMessage());
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

    /*private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }*/

}