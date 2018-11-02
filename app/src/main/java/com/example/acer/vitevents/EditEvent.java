package com.example.acer.vitevents;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.acer.vitevents.AsyncTasks.EditEventAsyncTask;
import com.example.acer.vitevents.Dialogs.Datepicker;
import com.example.acer.vitevents.Dialogs.Timepicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * EditEvent Activity edits the events uploaded to the server and replaces their poster in the firebase Storage with new one
 * The event details are updated through AsyncTask called as EditEventAsyncTask
 * The path file for FireBase storage is /posters/event_type/event_ID
 */


public class EditEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String LOG_TAG = EditEvent.class.getSimpleName();
    private EditText Title;
    private EditText Venue;
    private EditText Club;
    private EditText Guest;
    private EditText Org;
    private EditText Desp;
    private EditText Link;
    private EditText Fees;
    private EditText Date;
    private EditText Time;
    private TextView Image_name;
    private EditText Contact;
    private String ADD_URL;
    private JSONObject postData;
    private String event;
    private String id;
    private Uri uri;
    private Context mcontext;
    private String poster_avail;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        mcontext = getApplicationContext();
        Title =  findViewById(R.id.etitle);
        Venue =  findViewById(R.id.evenue);
        Club =  findViewById(R.id.eclub);
        Guest =  findViewById(R.id.eguest);
        Org =  findViewById(R.id.eorg);
        Desp =  findViewById(R.id.edescription);
        Link =  findViewById(R.id.elink);
        Fees =  findViewById(R.id.efees);
        Button update =  findViewById(R.id.update_event);
        Date =  findViewById(R.id.edate);
        Time =  findViewById(R.id.etimeed);
        Contact =  findViewById(R.id.econtact);
        ImageView poster =  findViewById(R.id.eadd_poster);
        Image_name =  findViewById(R.id.eimg_name);
        ImageView calenic =  findViewById(R.id.edateic);
        ImageView timeic =  findViewById(R.id.etimeic);
        TextView event1 =  findViewById(R.id.evet);
        ImageView deletePoster =  findViewById(R.id.delete_poster);

        Bundle b = getIntent().getExtras();
        event = b.getString("Event");
        id = b.getString("id");

        event1.setText(event);
        poster_avail = b.getString("poster");
        if (poster_avail.equals("true")) {
            Image_name.setText("Poster Available");
        } else if (poster_avail.equals("false")) {
            Image_name.setText("Poster Not Available");
        }

        /*Get details from Intents*/

        Title.setText(b.getString("title"));
        Venue.setText(b.getString("venue"));
        Date.setText(b.getString("date"));
        Time.setText(b.getString("time"));
        Club.setText(b.getString("club"));
        Desp.setText(b.getString("des"));
        Org.setText(b.getString("org"));
        Link.setText(b.getString("link"));
        Fees.setText(b.getString("fees"));
        Guest.setText(b.getString("guest"));
        Contact.setText(b.getString("contact"));


        switch (event) {
            case "Seminar":
                Guest.setVisibility(View.GONE);
                Fees.setVisibility(View.GONE);
                break;
            case "Workshop":
                Fees.setVisibility(View.VISIBLE);
                Guest.setVisibility(View.GONE);
                break;
            case "Conference":
                Fees.setVisibility(View.GONE);
                Guest.setVisibility(View.VISIBLE);
                break;
        }

        /*Function to delete poster in firebase*/

        deletePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseStorage = FirebaseStorage.getInstance();
                storageReference = firebaseStorage.getReference();
                StorageReference sr = storageReference.child("poster").child(event).child(id);
                sr.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        poster_avail = "false";
                        Image_name.setText("Poster Deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mcontext, "Poster not deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        /*Open DatePicker dialog*/

        calenic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Datepicker newFragment = new Datepicker();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        /*Open TimePicker dialog*/

        timeic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timepicker newFragment = new Timepicker();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        /*onClick function to select poster from gallery*/

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                startActivityForResult(intent, 100);
            }
        });

        /*Updates details to the server and poster image to the firebase storage using AsyncTask */

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String title = Title.getText().toString();
                final String venue = Venue.getText().toString();
                final String club = Club.getText().toString();
                final String guest = Guest.getText().toString();
                final String org = Org.getText().toString();
                final String contact = Contact.getText().toString();
                final String desp = Desp.getText().toString();
                final String link = Link.getText().toString();
                final String fees = Fees.getText().toString();
                final String d = Date.getText().toString();
                final String t = Time.getText().toString();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
                String x = d + " " + t;
                java.util.Date date = null;
                try {
                    date = sdf.parse(x);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final long millisec = date.getTime();

                if(validate()){
                switch (event) {
                    case "Seminar":
                        ADD_URL = "https://6b39f94b.ngrok.io" + "/updateseminar";

                        postData = new JSONObject();
                        try {
                            postData.put("id", id);
                            postData.put("title", title);
                            postData.put("millisec", millisec);
                            postData.put("venue", venue);
                            postData.put("Org", org);
                            postData.put("contact", contact);
                            postData.put("club", club);
                            postData.put("des", desp);
                            postData.put("link", link);
                            postData.put("poster", poster_avail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        break;

                    case "Conference":
                        ADD_URL = "https://6b39f94b.ngrok.io" + "/updateconference";

                        postData = new JSONObject();
                        try {
                            postData.put("id", id);
                            postData.put("title", title);
                            postData.put("millisec", millisec);
                            postData.put("venue", venue);
                            postData.put("Org", org);
                            postData.put("contact", contact);
                            postData.put("club", club);
                            postData.put("des", desp);
                            postData.put("link", link);
                            postData.put("guest", guest);
                            postData.put("poster", poster_avail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Workshop":
                        ADD_URL = "https://6b39f94b.ngrok.io" + "/updateworkshop";

                        postData = new JSONObject();
                        try {
                            postData.put("id", id);
                            postData.put("title", title);
                            postData.put("millisec", millisec);
                            postData.put("venue", venue);
                            postData.put("Org", org);
                            postData.put("contact", contact);
                            postData.put("club", club);
                            postData.put("des", desp);
                            postData.put("link", link);
                            postData.put("fees", fees);
                            postData.put("poster", poster_avail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        break;
                }
                if (isNetworkAvailable()) {
                    EditEventAsyncTask task = new EditEventAsyncTask();
                    task.setValues(postData,getBaseContext(),id,event,poster_avail,uri,getSupportFragmentManager());
                    task.execute(createUrl(ADD_URL));
                } else {
                    Toast.makeText(getBaseContext(), "Please connect to Internet", Toast.LENGTH_LONG).show();
                }

            }}
        });

    }

    /*EditText validator */

    private boolean validate() {
        boolean cancel = false;
        View focusView = null;
        boolean res = false;
        Title.setError(null);
        Venue.setError(null);
        Club.setError(null);
        Guest.setError(null);
        Org.setError(null);
        Desp.setError(null);
        Link.setError(null);
        Fees.setError(null);
        Date.setError(null);
        Time.setError(null);

        switch (event) {
            case "Seminar":

                if (TextUtils.isEmpty(Title.getText().toString())) {
                    Title.setError(getString(R.string.error_field_required));
                    focusView = Title;
                    cancel = true;
                } else if (TextUtils.isEmpty(Venue.getText().toString())) {
                    Venue.setError(getString(R.string.error_field_required));
                    focusView = Venue;
                    cancel = true;
                } else if (TextUtils.isEmpty(Date.getText().toString())) {
                    Date.setError(getString(R.string.error_field_required));
                    focusView = Date;
                    cancel = true;
                } else if (TextUtils.isEmpty(Time.getText().toString())) {
                    Time.setError(getString(R.string.error_field_required));
                    focusView = Time;
                    cancel = true;
                } else if (TextUtils.isEmpty(Club.getText().toString())) {
                    Club.setError(getString(R.string.error_field_required));
                    focusView = Club;
                    cancel = true;
                }else if (TextUtils.isEmpty(Org.getText().toString())) {
                    Org.setError(getString(R.string.error_field_required));
                    focusView = Org;
                    cancel = true;
                }else if (TextUtils.isEmpty(Contact.getText().toString())) {
                    Contact.setError(getString(R.string.error_field_required));
                    focusView = Contact;
                    cancel = true;
                }  else if (TextUtils.isEmpty(Desp.getText().toString())) {
                    Desp.setError(getString(R.string.error_field_required));
                    focusView = Desp;
                    cancel = true;
                }
                break;
            case "Workshop":
                if (TextUtils.isEmpty(Title.getText().toString())) {
                    Title.setError(getString(R.string.error_field_required));
                    focusView = Title;
                    cancel = true;
                } else if (TextUtils.isEmpty(Venue.getText().toString())) {
                    Venue.setError(getString(R.string.error_field_required));
                    focusView = Venue;
                    cancel = true;
                } else if (TextUtils.isEmpty(Date.getText().toString())) {
                    Date.setError(getString(R.string.error_field_required));
                    focusView = Date;
                    cancel = true;
                } else if (TextUtils.isEmpty(Club.getText().toString())) {
                    Club.setError(getString(R.string.error_field_required));
                    focusView = Club;
                    cancel = true;
                }else if (TextUtils.isEmpty(Time.getText().toString())) {
                    Time.setError(getString(R.string.error_field_required));
                    focusView = Time;
                    cancel = true;
                } else if (TextUtils.isEmpty(Org.getText().toString())) {
                    Org.setError(getString(R.string.error_field_required));
                    focusView = Org;
                    cancel = true;
                }  else if (TextUtils.isEmpty(Contact.getText().toString())) {
                    Contact.setError(getString(R.string.error_field_required));
                    focusView = Contact;
                    cancel = true;
                }  else if (TextUtils.isEmpty(Desp.getText().toString())) {
                    Desp.setError(getString(R.string.error_field_required));
                    focusView = Desp;
                    cancel = true;
                } else if (TextUtils.isEmpty(Fees.getText().toString())) {
                    Fees.setError(getString(R.string.error_field_required));
                    focusView = Fees;
                    cancel = true;
                }

                break;
            case "Conference":
                if (TextUtils.isEmpty(Title.getText().toString())) {
                    Title.setError(getString(R.string.error_field_required));
                    focusView = Title;
                    cancel = true;
                } else if (TextUtils.isEmpty(Venue.getText().toString())) {
                    Venue.setError(getString(R.string.error_field_required));
                    focusView = Venue;
                    cancel = true;
                } else if (TextUtils.isEmpty(Date.getText().toString())) {
                    Date.setError(getString(R.string.error_field_required));
                    focusView = Date;
                    cancel = true;
                } else if (TextUtils.isEmpty(Time.getText().toString())) {
                    Time.setError(getString(R.string.error_field_required));
                    focusView = Time;
                    cancel = true;
                } else if (TextUtils.isEmpty(Club.getText().toString())) {
                    Club.setError(getString(R.string.error_field_required));
                    focusView = Club;
                    cancel = true;
                } else if (TextUtils.isEmpty(Guest.getText().toString())) {
                    Guest.setError(getString(R.string.error_field_required));
                    focusView = Guest;
                    cancel = true;
                } else if (TextUtils.isEmpty(Org.getText().toString())) {
                    Org.setError(getString(R.string.error_field_required));
                    focusView = Org;
                    cancel = true;
                }  else if (TextUtils.isEmpty(Contact.getText().toString())) {
                    Contact.setError(getString(R.string.error_field_required));
                    focusView = Contact;
                    cancel = true;
                }  else if (TextUtils.isEmpty(Desp.getText().toString())) {
                    Desp.setError(getString(R.string.error_field_required));
                    focusView = Desp;
                    cancel = true;
                }


                break;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            res = true;
        }
        return res;
    }

    /*Check whether the network is available*/

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo active = cm.getActiveNetworkInfo();
        return active != null && active.isConnected();

    }

    //Image selection activity

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            uri = data.getData();
            String finalpath = getRealPathFromURI(uri, this);
            Image_name.setText(finalpath);
            poster_avail = "true";
        }
    }

    //Get realpath for sharing image

    private String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            return cursor.getString(column_index);
            // cursor.close();
        }
        // cursor.close();
        return null;
    }

    /*convert datepicker date to user needed Format*/

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        String date = i2 + "/" + i1 + "/" + i;
        Date.setText(date);

    }

    /*convert Timepicker time to user needed Format*/

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
        String timeSet ;
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String min ;
        if (minutes < 10)
            min = "0" + minutes;
        else
            min = String.valueOf(minutes);

        String time = String.valueOf(hour) + ':' +
                min + " " + timeSet;
        Time.setText(time);
    }

    /*Function to convert string to Url*/

    private URL createUrl(String stringUrl) {
        URL url ;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }
}


