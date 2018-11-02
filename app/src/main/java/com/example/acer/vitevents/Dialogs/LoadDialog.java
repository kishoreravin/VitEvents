package com.example.acer.vitevents.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.vitevents.R;

/**
 * This dialogFragment return a Dialog with Progress bar
 * This Dialog is shown when EventAsync Task is called
 */

public class LoadDialog extends DialogFragment {

    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        d.setCanceledOnTouchOutside(false);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.load_dialog,container,false);

    }
}
