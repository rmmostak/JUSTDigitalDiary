package com.blogspot.skferdous.justdigitaldiary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authentication extends AppCompatActivity {

    private EditText email, pass, confirmPass;
    private TextView warning, signUp;
    private Button signIn;

    private EditText signupEmail, signupPass, signupConfirmPass;
    private TextView signupWarning, signupSignIn;
    private Button signupSignUpButton;

    private LinearLayout signInLayout, signUpLayout;

    private FirebaseAuth firebaseAuth;
    //private SignInButton googleSignInUp;

    private static final int RC_SIGN_IN = 123;
    private static final String TAG ="DIGITALDIARYJUST" ;
    //GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        CoordinatorLayout coordinatorLayout;
        coordinatorLayout=findViewById(R.id.coordinator);
        if (!isConnected()) {
            /*new AlertDialog.Builder(this)
                    .setIcon(R.drawable.logo)
                    .setTitle("You are offline!")
                    .setMessage("Please connect to the internet and try again, Thank you!")
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    }).show();*/
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

        signupEmail = findViewById(R.id.signupEmail);
        signupPass = findViewById(R.id.signupPass);
        signupConfirmPass = findViewById(R.id.signupConfirmPass);
        signupWarning = findViewById(R.id.signupWarning);
        signupSignUpButton = findViewById(R.id.signupSignUpButton);
        signupSignIn = findViewById(R.id.signupSignIn);
        signInLayout = findViewById(R.id.signInLayout);
        signUpLayout = findViewById(R.id.signUpLayout);

        //googleSignInUp = findViewById(R.id.googleSignIn);

        /*// Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.googleSignInUp).setOnClickListener(view -> signIn());*/

        signupSignIn.setOnClickListener(v_ -> {
            signInLayout.setVisibility(View.VISIBLE);
            signUpLayout.setVisibility(View.GONE);
            actionForSignIn();
        });

        signupSignUpButton.setOnClickListener(v -> {
            actionForSignUp();
        });

        signIn.setOnClickListener(v -> {
            signUpLayout.setVisibility(View.GONE);
            signInLayout.setVisibility(View.VISIBLE);

            //onCreate(savedInstanceState);

            signUp.setOnClickListener(v1 -> {
                signUpLayout.setVisibility(View.VISIBLE);
                signInLayout.setVisibility(View.GONE);
            });

            actionForSignIn();
        });

        signUp.setOnClickListener(v -> {
            signUpLayout.setVisibility(View.VISIBLE);
            signInLayout.setVisibility(View.GONE);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(Authentication.this, SplashScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void actionForSignIn() {

        signUp.setOnClickListener(v -> {
            signUpLayout.setVisibility(View.VISIBLE);
            signInLayout.setVisibility(View.GONE);

            //onCreate(sav);
        });

        signIn.setOnClickListener(v -> {
            String _mail = email.getText().toString().trim();
            String _pass = pass.getText().toString().trim();

            if (!TextUtils.isEmpty(_mail)) {

                if (Patterns.EMAIL_ADDRESS.matcher(_mail).matches()) {

                    if (!TextUtils.isEmpty(_pass)) {

                        getDataForExistUser(_mail, _pass);

                    } else {
                        pass.setError("Enter your password!!");
                        pass.requestFocus();
                        return;
                    }
                } else {
                    email.setError("Enter a valid email address!!");
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

    private void actionForSignUp() {
        signupSignIn.setOnClickListener(v -> {
            signInLayout.setVisibility(View.VISIBLE);
            signUpLayout.setVisibility(View.GONE);
            actionForSignIn();
        });

        String mail = signupEmail.getText().toString().trim();
        String p1 = signupPass.getText().toString();
        String p2 = signupConfirmPass.getText().toString();

        if (!TextUtils.isEmpty(mail)) {

            if (Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {

                if (!TextUtils.isEmpty(p1)) {

                    if (!TextUtils.isEmpty(p2)) {

                        if (p1.equals(p2)) {

                            signUpforNewUser(mail, p2);

                            signupEmail.setText("");
                            signupPass.setText("");
                            signupConfirmPass.setText("");

                            signInLayout.setVisibility(View.VISIBLE);
                            signUpLayout.setVisibility(View.GONE);

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

    private void signUpforNewUser(String mail, String p2) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Creating your account, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        firebaseAuth.createUserWithEmailAndPassword(mail, p2).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Alert!");
                builder.setIcon(R.drawable.logo);
                builder.setMessage("You have created account successfully, Please login to enjoy this app!!");
                builder.setPositiveButton("OK", (dialog1, which) -> {
                    signInLayout.setVisibility(View.VISIBLE);
                    signUpLayout.setVisibility(View.GONE);
                });
                builder.show();

            } else {
                signupWarning.setText(task.getException().getMessage());
                dialog.dismiss();
            }
        });
    }

    private void getDataForExistUser(String mail, String p1) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Logging in, please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        firebaseAuth.signInWithEmailAndPassword(mail, p1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                email.setText("");
                pass.setText("");
                dialog.dismiss();
                Intent intent = new Intent(Authentication.this, SplashScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                dialog.dismiss();
                warning.setText(task.getException().getMessage());
            }
        });
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