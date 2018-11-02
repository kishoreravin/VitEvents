package com.example.acer.vitevents.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.acer.vitevents.AsyncTasks.DeleteAsyncTask;
import com.example.acer.vitevents.R;

/**
 * DeleteDialog returns a Custom Dialog which has two buttons
 * The setCon function receives context, type of eveent(eg., Seminar,Conference,Workshop) and the ID of the event to be deleted
 * Cancel button dismisses the Dialog
 * Delete button executes DeleteAsyncTask which deletes event in server which has the specified ID and the poster in Firebase Storage
 */

public class DeleteDialog extends DialogFragment {

    private Context mcontext;
    private String From;
    private String ID;


    public void setCon(Context mc,String fr,String id){
        mcontext= mc;
        From = fr;
        ID = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.deleteprompt,container,false);
        Button cancel =v.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteDialog.this.dismiss();
            }
        });
        Button del =  v.findViewById(R.id.delete);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask();
                deleteAsyncTask.setContext(mcontext,From,ID,getFragmentManager());
                deleteAsyncTask.execute();
                DeleteDialog.this.dismiss();
            }
        });
        return v;
    }
}
