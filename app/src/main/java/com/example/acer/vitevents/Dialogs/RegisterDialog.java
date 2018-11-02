package com.example.acer.vitevents.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.acer.vitevents.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This dialog returns register_dialog view
 * It has two edittext to enter Email address and password
 * It has a button to register the email and password in the Firebase Authentication and sends EmailVerification
 */

public class RegisterDialog extends DialogFragment {


    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if(d!=null){
            d.getWindow().setLayout(900,1100);
        }
        assert d != null;
        d.setCanceledOnTouchOutside(true);
    }
    private String pass;
    private String em;
    private EditText email;
    private EditText password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register_dialog, container, false);

        email =  v.findViewById(R.id.remail);

        password = v.findViewById(R.id.rpassword);


        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Button reg = v.findViewById(R.id.register_user);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                em = email.getText().toString();
                pass = password.getText().toString();

                if (Validate()) {
                    firebaseAuth.createUserWithEmailAndPassword(em, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendemailverify();
                            } else {
                                Toast.makeText(getContext(), "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        return v;

    }

    private void sendemailverify() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Registered Successful and email verification send", Toast.LENGTH_SHORT).show();

                    }
                    RegisterDialog.this.dismiss();
                    FirebaseAuth.getInstance().signOut();
                }
            });
        }
    }
    private boolean Validate() {

        boolean res = false;
        // Reset errors.
        email.setError(null);
        password.setError(null);


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(pass)){
            password.setError(getString(R.string.error_field_required));
            focusView = password;
            cancel = true;
        }else if(!isPasswordValid(pass)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(em)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(em)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            res = true;
        }

        return res;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@vit.ac.in")&&email.contains("@vit.ac.in");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 4;
    }

}
