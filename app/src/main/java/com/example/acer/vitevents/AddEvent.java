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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.acer.vitevents.AsyncTasks.EditEventAsyncTask;
import com.example.acer.vitevents.Dialogs.Datepicker;
import com.example.acer.vitevents.Dialogs.Timepicker;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * AddEvent Activity adds events to the server and uploads their poster to the firebase Storage
 * The event details are uploaded through AsyncTask called as EditEventAsyncTask
 * The path file for FireBase storage is /posters/event_type/event_ID
 */

public class AddEvent extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static final String LOG_TAG = AddEvent.class.getSimpleName();
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
    private String event_type;
    private String ADD_URL;
    private JSONObject postData;
    private String ID;
    private String poster_avail = "false";
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addevent);
        Title = findViewById(R.id.title);
        Venue = findViewById(R.id.venue);
        Club = findViewById(R.id.club);
        Guest = findViewById(R.id.guest);
        Org = findViewById(R.id.org);
        Desp = findViewById(R.id.description);
        Link = findViewById(R.id.link);
        Fees = findViewById(R.id.fees);
        Button send = findViewById(R.id.add_event);
        Spinner spinner = findViewById(R.id.spinner);
        Date = findViewById(R.id.date);
        Time = findViewById(R.id.timeed);
        Contact = findViewById(R.id.contact);
        ImageView poster = findViewById(R.id.add_poster);
        Image_name = findViewById(R.id.img_name);
        ImageView calenic = findViewById(R.id.dateic);
        ImageView timeic = findViewById(R.id.timeic);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        List<String> list = new ArrayList<>();
        list.add("Seminar");
        list.add("Workshop");
        list.add("Conference");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_items, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        /* Get the event type from spinner*/

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                event_type = adapterView.getItemAtPosition(i).toString();
                switch (event_type) {
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

        /*Sends details to the server and poster image to the firebase storage using AsyncTask*/

        send.setOnClickListener(new View.OnClickListener() {
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


                if (validate()) {
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
                    String x = d + " " + t;
                    Date date = null;
                    try {
                        date = sdf.parse(x);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    final long millisec = date.getTime();
                    Random rand = new Random();
                    final Integer id = rand.nextInt(9999999) + 1111111;
                    ID = String.valueOf(id);
                    switch (event_type) {
                        case "Seminar":
                            ADD_URL = "https://6b39f94b.ngrok.io" + "/newseminar";

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
                            ADD_URL = "https://6b39f94b.ngrok.io" + "/newconference";

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
                            ADD_URL = "https://6b39f94b.ngrok.io" + "/newworkshop";

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
                        task.setValues(postData,getBaseContext(),ID,event_type,poster_avail,uri,getSupportFragmentManager());
                        task.execute(createUrl(ADD_URL));
                    } else {
                        Toast.makeText(getBaseContext(), "Please connect to Internet", Toast.LENGTH_LONG).show();
                    }

                }

            }
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

        switch (event_type) {
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
        }
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
        String timeSet;
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

        String min;
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
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }
}




