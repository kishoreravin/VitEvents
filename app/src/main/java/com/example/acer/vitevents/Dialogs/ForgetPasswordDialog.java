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
import com.google.firebase.auth.FirebaseAuth;

/**
 * This dialog prompts the user to enter the Registered email so that password reset link is send to the verified email address
 */
public class ForgetPasswordDialog extends DialogFragment {
    

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if(d!=null){
            d.getWindow().setLayout(900,750);
        }
        assert d != null;
        d.setCanceledOnTouchOutside(true);
    }


    private String em;
    private EditText email;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.forgotpassword, container, false);

        email =  v.findViewById(R.id.regemail);

        Button reset =  v.findViewById(R.id.submit);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                em = email.getText().toString().trim();
                if (!TextUtils.isEmpty(em)) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(em).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Password Reset link is send to the mail", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Email entered is not valid", Toast.LENGTH_SHORT).show();

                }
                ForgetPasswordDialog.this.dismiss();
            }
        });
        return v;
    }
}
