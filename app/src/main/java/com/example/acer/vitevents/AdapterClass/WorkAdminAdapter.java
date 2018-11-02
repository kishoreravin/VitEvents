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
import com.example.acer.vitevents.WordClass.WorkshopWord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * WorkAdminAdapter is a custom Adapter to display Admin perspective of Workshops in Viewevents
 * This adapter returns Custom View for list
 * This adapter has two buttons to edit and delete events
 * Edit button starts intent to EditEvent.class to edit the given details
 * Delete button opens dialog to confirm delete before actually deleting the event
 * And a call button to start intent to make a call
 */

public class WorkAdminAdapter extends ArrayAdapter<WorkshopWord> {

    private Context mcontext;
    private FragmentManager fragmentManager;

    public WorkAdminAdapter(Context context, ArrayList<WorkshopWord> words,FragmentManager fm) {
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
                    R.layout.work_admin, parent, false);
        }
        final WorkshopWord cword = getItem(position);
        assert cword != null;
        TextView Title =  listItemView.findViewById(R.id.as_title);
        Title.setText(cword.getTitle());
        TextView date =  listItemView.findViewById(R.id.as_date);
        TextView time =  listItemView.findViewById(R.id.as_time);

        Long ml = Long.valueOf(cword.getMillisec());
        Date dateobject = new Date(ml);
        final String fm = formatDate(dateobject);
        date.setText(fm);
        final String tm = formatTime(dateobject);
        time.setText(tm);

        TextView Venue =  listItemView.findViewById(R.id.as_venue);
        Venue.setText(cword.getVenue());

        TextView Club =  listItemView.findViewById(R.id.as_club);
        Club.setText(cword.getClub());

        TextView Org =  listItemView.findViewById(R.id.as_org);
        Org.setText(cword.getOrg());

        TextView Des =  listItemView.findViewById(R.id.as_des);
        Des.setText(cword.getDes());

        TextView fees =  listItemView.findViewById(R.id.as_fees);
        fees.setText(cword.getFees());

        Button edit =  listItemView.findViewById(R.id.edit_event);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent in = new Intent(mcontext,EditEvent.class);
                in.putExtra("Event","Workshop");
                in.putExtra("id",cword.getId());
                in.putExtra("title",cword.getTitle());
                in.putExtra("date",fm);
                in.putExtra("time",tm);
                in.putExtra("venue",cword.getVenue());
                in.putExtra("club",cword.getClub());
                in.putExtra("org",cword.getOrg());
                in.putExtra("des",cword.getDes());
                in.putExtra("fees",cword.getFees());
                in.putExtra("link",cword.getLink());
                in.putExtra("contact",cword.getContact());
                in.putExtra("guest","..");
                in.putExtra("poster",cword.getPoster());
                mcontext.startActivity(in);

            }
        });
        Button delete =  listItemView.findViewById(R.id.delete_event);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteDialog dd = new DeleteDialog();
                dd.setCon(getContext(),"Workshop",cword.getId());
                dd.show(fragmentManager,"Delete..");
            }
        });

        ImageView call =  listItemView.findViewById(R.id.acall);
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