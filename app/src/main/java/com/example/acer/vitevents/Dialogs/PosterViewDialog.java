package com.example.acer.vitevents.Dialogs;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.acer.vitevents.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * PosterView dialog opens when View poster button is clicked
 * This dialog has setCon function which receives event type and ID
 * This dialog has picasso library which loads the url of the poster uploaded in the firebase storage in the ImageView
 */

public class PosterViewDialog extends DialogFragment {

    private String From;
    private String ID;
    private  StorageReference storageReference ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if(d!=null){
            d.getWindow().setLayout(1000,1000);
        }
        d.setCanceledOnTouchOutside(true);
    }

    public void setCon(String fr, String id){
        From = fr;
        ID = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.poster_view,container,false);
        final ImageView poster = v.findViewById(R.id.poster_image);
        StorageReference sr = storageReference.child("poster").child(From).child(ID);
        sr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(poster);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        return v;
    }
}
