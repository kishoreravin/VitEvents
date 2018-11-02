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

import com.example.acer.vitevents.WordClass.ConfWord;
import com.example.acer.vitevents.Dialogs.DeleteDialog;
import com.example.acer.vitevents.EditEvent;
import com.example.acer.vitevents.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * ConAdminAdapter is a custom Adapter to display Admin perspective of Conferences in Viewevents
 * This adapter returns Custom View for list
 * This adapter has two buttons to edit and delete events
 * Edit button starts intent to EditEvent.class to edit the given details
 * Delete button opens dialog to confirm delete before actually deleting the event
 * And a call button to start intent to make a call
 */

public class ConAdminAdapter extends ArrayAdapter<ConfWord> {
    private  Context mcontext;
    private FragmentManager fragmentManager;

    public ConAdminAdapter(Context context, ArrayList<ConfWord> words,FragmentManager fm) {
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
                    R.layout.con_admin, parent, false);
        }

        final ConfWord cword = getItem(position);

        TextView Title =  listItemView.findViewById(R.id.ec_title);
        Title.setText(cword.getTitle());

        TextView date =  listItemView.findViewById(R.id.ec_date);
        TextView time =  listItemView.findViewById(R.id.ec_time);

        Long ml = Long.valueOf(cword.getMillisec());
        Date dateobject = new Date(ml);
        final String fm = formatDate(dateobject);
        date.setText(fm);
        final String tm = formatTime(dateobject);
        time.setText(tm);

        TextView Venue =  listItemView.findViewById(R.id.ec_venue);
        Venue.setText(cword.getVenue());

        TextView Club =  listItemView.findViewById(R.id.ec_club);
        Club.setText(cword.getClub());

        TextView org =  listItemView.findViewById(R.id.ec_org);
        org.setText(cword.getOrg());

        TextView Des =  listItemView.findViewById(R.id.ec_des);
        Des.setText(cword.getDes());

        TextView guest =  listItemView.findViewById(R.id.ec_guest);
        guest.setText(cword.getGuest());

        Button edit =  listItemView.findViewById(R.id.cedit_event);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mcontext,EditEvent.class);
                in.putExtra("Event","Conference");
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
                in.putExtra("guest",cword.getGuest());
                in.putExtra("poster",cword.getPoster());
                mcontext.startActivity(in);

            }
        });
        Button delete =  listItemView.findViewById(R.id.cdelete_event);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteDialog dd = new DeleteDialog();
                dd.setCon(getContext(),"Conference",cword.getId());
                dd.show(fragmentManager,"Delete..");
            }
        });
        ImageView call =  listItemView.findViewById(R.id.ec_call);
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
        return timeFormat.format(fm);
    }

    private String formatDate(Date dateobject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(dateobject);
    }
    }
