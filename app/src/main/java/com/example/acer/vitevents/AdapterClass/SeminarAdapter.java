package com.example.acer.vitevents.AdapterClass;

import android.annotation.SuppressLint;
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

import com.example.acer.vitevents.Dialogs.PosterViewDialog;
import com.example.acer.vitevents.R;
import com.example.acer.vitevents.WordClass.SeminarWord;
import com.example.acer.vitevents.Utils.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * SeminarAdapter is used to view the Student perspective of Seminars in ViewEvent
 * This Adapter returns Custom View for the list
 * This adapter has 3 buttons
 * Join button starts intent to browser to open the link given
 * Call button starts intent to make a call to the given number
 * View poster button opens a dialog to show the Poster uploaded in the firebase storage
 */

public class SeminarAdapter extends ArrayAdapter<SeminarWord> {

    private Context mcontext;
    private FragmentManager fragmentManager;
    public SeminarAdapter(Context context, ArrayList<SeminarWord> words,FragmentManager fm) {
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
                    R.layout.sem_listview, parent, false);
        }

        final SeminarWord cword = getItem(position);

        TextView Title =  listItemView.findViewById(R.id.sem_title);
        Title.setText(cword.getTitle());

        TextView date =  listItemView.findViewById(R.id.sem_date);
        TextView time =  listItemView.findViewById(R.id.sem_time);

        Long ml = Long.valueOf(cword.getMillisec());
        Date dateobject = new Date(ml);
        String fm = formatDate(dateobject);
        date.setText(fm);
        String tm = formatTime(dateobject);
        time.setText(tm);

        TextView Venue =  listItemView.findViewById(R.id.sem_venue);
        Venue.setText(cword.getVenue());

        TextView Club =  listItemView.findViewById(R.id.sem_club);
        Club.setText(cword.getClub());

        TextView Org =  listItemView.findViewById(R.id.sem_org);
        Org.setText(cword.getOrg());

        TextView Des =  listItemView.findViewById(R.id.sem_des);
        Des.setText(cword.getDes());

        ViewHolder Sem_holder;
        Sem_holder = new ViewHolder();
        Sem_holder.join =  listItemView.findViewById(R.id.sem_join);
        Sem_holder.add_clender =  listItemView.findViewById(R.id.sem_add_calender);
        Sem_holder.callbutton =  listItemView.findViewById(R.id.sem_call);
        Sem_holder.view_poster =  listItemView.findViewById(R.id.sem_view_poster);
        Sem_holder.join.setOnClickListener(new View.OnClickListener() {
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
        Sem_holder.callbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pn = cword.getContact();
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",pn,null));
                mcontext.startActivity(i);
            }
        });
        Sem_holder.view_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cword.getPoster().equals("true")){
                    PosterViewDialog post = new PosterViewDialog();
                    post.setCon("Seminar",cword.getId());
                    post.show(fragmentManager,"Poster..");
                }
            }
        });
        Sem_holder.add_clender.setOnClickListener(new View.OnClickListener() {
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

        return listItemView;
    }
    private String formatTime(Date fm) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm aa");
        return  timeFormat.format(fm);
    }

    private String formatDate(Date dateobject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return  dateFormat.format(dateobject);
    }
}
