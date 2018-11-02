package com.example.acer.vitevents.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.vitevents.WordClass.ConfWord;
import com.example.acer.vitevents.Dialogs.PosterViewDialog;
import com.example.acer.vitevents.R;
import com.example.acer.vitevents.Utils.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * ConfAdapter is used to view the Student perspective of Conferences in ViewEvent
 * This Adapter returns Custom View for the list
 * This adapter has 3 buttons
 * Join button starts intent to browser to open the link given
 * Call button starts intent to make a call to the given number
 * View poster button opens a dialog to show the Poster uploaded in the firebase storage
 */

public class ConfAdapter extends ArrayAdapter<ConfWord> {
    private Context mcontext;
    private FragmentManager fragmentManager;

    public ConfAdapter(Context context, ArrayList<ConfWord> words,FragmentManager fm) {
        super(context, 0, words);
        mcontext = context;
        fragmentManager =fm;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.con_listview, parent, false);
        }

        final ConfWord cword = getItem(position);

        TextView Title =  listItemView.findViewById(R.id.c_title);
        Title.setText(cword.getTitle());

        TextView date =  listItemView.findViewById(R.id.c_date);
        TextView time =  listItemView.findViewById(R.id.c_time);

        Long ml = Long.valueOf(cword.getMillisec());
        Date dateobject = new Date(ml);
        String fm = formatDate(dateobject);
        date.setText(fm);
        String tm = formatTime(dateobject);
        time.setText(tm);

        TextView Venue =  listItemView.findViewById(R.id.c_venue);
        Venue.setText(cword.getVenue());

        TextView Club =  listItemView.findViewById(R.id.c_club);
        Club.setText(cword.getClub());

        TextView org =  listItemView.findViewById(R.id.c_org);
        org.setText(cword.getOrg());

        TextView Des =  listItemView.findViewById(R.id.c_des);
        Des.setText(cword.getDes());

        TextView guest =  listItemView.findViewById(R.id.c_guest);
        guest.setText(cword.getGuest());

        ViewHolder c_holder;
        c_holder = new ViewHolder();
        c_holder.join =  listItemView.findViewById(R.id.c_join);
        c_holder.add_clender =  listItemView.findViewById(R.id.c_add_calender);
        c_holder.callbutton =  listItemView.findViewById(R.id.c_call);
        c_holder.view_poster =  listItemView.findViewById(R.id.c_view_poster);
        c_holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = cword.getLink();
                Intent in = new Intent(Intent.ACTION_VIEW);
                try {
                    in.setData(Uri.parse(url));
                    mcontext.startActivity(in);
                }catch (Exception e){
                    Toast.makeText(mcontext,"Error in parsing url "+e,Toast.LENGTH_SHORT).show();
                }
            }
        });

        c_holder.view_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cword.getPoster().equals("true")){
                    PosterViewDialog post = new PosterViewDialog();
                    post.setCon("Conference",cword.getId());
                    post.show(fragmentManager,"Poster..");
                }
            }
        });

        c_holder.add_clender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long starttime = Long.parseLong(cword.getMillisec());
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, starttime)
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, starttime+600000)
                        .putExtra(CalendarContract.Events.TITLE, cword.getTitle())
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, cword.getVenue())
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                mcontext.startActivity(intent);
            }
        });

        c_holder.callbutton.setOnClickListener(new View.OnClickListener() {
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
