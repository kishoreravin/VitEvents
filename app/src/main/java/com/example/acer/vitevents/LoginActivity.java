package com.example.acer.vitevents;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.acer.vitevents.Dialogs.ForgetPasswordDialog;
import com.example.acer.vitevents.Dialogs.RegisterDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Login Activity is the Launching Activity od the application
 * It uses Firebase Authentication fro login
 * Login is validated and intents are send according to Admin and Student
 * This Activity will be finished when some user has already logged in
 * This Activity has three Buttons
 * SignIn button logs in the User
 * Registration Button opens RegisterUser dialog where user can register their Vit email id
 * ForgetPassword Button opens forgetPassword dialog where user can enter their registered email to send reset password link to that email
 */
public class LoginActivity extends AppCompatActivity {

    private ProgressBar pg;
    private EditText mEmailView;
    private EditText mPasswordView;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = firebaseAuth.getCurrentUser();
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fragmentManager = getSupportFragmentManager();

        /*Check whether the user has already logged in*/

        if (user != null) {
            String email = user.getEmail();
            finish();

            /*Student validation with putExtra value Student to notify the viewevent class that intent is from Student*/

            if (email.contains("rohith")) {
                Intent i = new Intent(getBaseContext(), ViewEvent.class);
                i.putExtra("From", "Student");
                startActivity(i);
            }

            /*Student validation with putExtra value Admin to notify the viewevent class that intent is from Admin*/

            else if (email.contains("kishore")) {
                Intent i = new Intent(getBaseContext(), ViewEvent.class);
                i.putExtra("From", "Admin");
                startActivity(i);
            }
        }

        pg =  findViewById(R.id.login_progress);

        // Set up the login form.
        mEmailView =  findViewById(R.id.email);

        mPasswordView =  findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton =  findViewById(R.id.email_sign_in_button);

        /*Sign in button to Login*/

        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg.setVisibility(View.VISIBLE);
                attemptLogin();
            }
        });

        /*Registration button to register new user through Register Dialog*/

        Button reg =  findViewById(R.id.register);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment regDialog = new RegisterDialog();
                regDialog.show(fragmentManager, "Register..");

            }
        });

        /*Forget Password Button to show forget password dialog*/

        Button forget =  findViewById(R.id.forget_pass);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgetPasswordDialog forgetPasswordDialog = new ForgetPasswordDialog();
                forgetPasswordDialog.show(fragmentManager, "Reset...");
            }
        });
    }


    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            login(email, password);
        }
    }

    /*Main Login Function*/

    private void login(String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {


            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                pg.setVisibility(View.GONE);
                if (task.isSuccessful()) {

                    user = firebaseAuth.getCurrentUser();

                    /*Checks whether the Registered user has verified his/her email */

                    if (user.isEmailVerified()) {
                        Toast.makeText(getBaseContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        String email = user.getEmail();
                        finish();
                        if (email.contains("rohith")) {
                            Intent i = new Intent(getBaseContext(), ViewEvent.class);
                            i.putExtra("From", "Student");
                            startActivity(i);
                        } else if (email.contains("kishore")) {
                            Intent i = new Intent(getBaseContext(), ViewEvent.class);
                            i.putExtra("From", "Admin");
                            startActivity(i);
                        }
                    } else {
                        firebaseAuth.signOut();
                        Toast.makeText(getBaseContext(), "Verify your Email", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*Checks whether entered email belons to VIT University*/

    private boolean isEmailValid(String email) {
        return email.contains("@vit.ac.in") && email.contains("@vit.ac.in");
    }

    /*Check whether the length of password is more than 4*/
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


}
