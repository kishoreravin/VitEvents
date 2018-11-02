package com.example.acer.vitevents.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.vitevents.Dialogs.DeleteDialog;
import com.example.acer.vitevents.EditEvent;
import com.example.acer.vitevents.R;
import com.example.acer.vitevents.WordClass.SeminarWord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * SemAdminAdapter is a custom Adapter to display Admin perspective of Seminars in Viewevents
 * This adapter returns Custom View for list
 * This adapter has two buttons to edit and delete events
 * Edit button starts intent to EditEvent.class to edit the given details
 * Delete button opens dialog to confirm delete before actually deleting the event
 * And a call button to start intent to make a call
 */


public class SemAdminAdapter extends ArrayAdapter<SeminarWord> {
    private  Context mcontext;
    private  FragmentManager fragmentManager;
    public SemAdminAdapter(Context context, ArrayList<SeminarWord> words,FragmentManager fm) {
        super(context, 0, words);
        mcontext = context;
        fragmentManager = fm;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.sem_admin, parent, false);
        }

        final SeminarWord cword = getItem(position);

        TextView Title =  listItemView.findViewById(R.id.esem_title);
        Title.setText(cword.getTitle());

        TextView date =  listItemView.findViewById(R.id.esem_date);
        TextView time =  listItemView.findViewById(R.id.esem_time);

        Long ml = Long.valueOf(cword.getMillisec());
        Date dateobject = new Date(ml);
        final String fm = formatDate(dateobject);
        date.setText(fm);
        final String tm = formatTime(dateobject);
        time.setText(tm);

        TextView Venue =  listItemView.findViewById(R.id.esem_venue);
        Venue.setText(cword.getVenue());

        TextView Club =  listItemView.findViewById(R.id.esem_club);
        Club.setText(cword.getClub());

        TextView Org =  listItemView.findViewById(R.id.esem_org);
        Org.setText(cword.getOrg());

        TextView Des =  listItemView.findViewById(R.id.esem_des);
        Des.setText(cword.getDes());
        Button edit =  listItemView.findViewById(R.id.eedit_event);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mcontext,EditEvent.class);
                in.putExtra("Event","Seminar");
                in.putExtra("id",cword.getId());
                in.putExtra("title",cword.getTitle());
                in.putExtra("date",fm);
                in.putExtra("time",tm);
                in.putExtra("venue",cword.getVenue());
                in.putExtra("club",cword.getClub());
                in.putExtra("org",cword.getOrg());
                in.putExtra("des",cword.getDes());
                in.putExtra("fees","..");
                in.putExtra("link",cword.getLink());
                in.putExtra("contact",cword.getContact());
                in.putExtra("guest","..");
                in.putExtra("poster",cword.getPoster());
                mcontext.startActivity(in);

            }
        });
        Button delete =  listItemView.findViewById(R.id.edelete_event);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteDialog dd = new DeleteDialog();
                dd.setCon(getContext(),"Seminar",cword.getId());
                dd.show(fragmentManager,"Delete..");

            }
        });

        ImageView call =  listItemView.findViewById(R.id.esem_call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pn = cword.getContact();
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",pn,null));
                mcontext.startActivity(i);
            }
        });


        return listItemView;

    }
    private String formatTime(Date fm) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm aa");
        return  timeFormat.format(fm);
    }

    private String formatDate(Date dateobject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return  dateFormat.format(dateobject);
    }
}
