package com.example.acer.vitevents.AsyncTasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.acer.vitevents.Dialogs.LoadDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * This Async Task is adds new and edited events to the server and also uploads posters to the firebase storage
 * This AyncTask is executed from AddEvent and EditEvent Activity
 */

public class EditEventAsyncTask extends AsyncTask<URL, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context mcontext;
    private JSONObject postData;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();
    private String ID;
    private String event_type;
    private String poster_avail;
    private Uri uri;
    private DialogFragment df;
    private FragmentManager fragmentManager;

    public void setValues(JSONObject jsonObject, Context context, String id, String event, String poster, Uri u, FragmentManager fm) {
        ID = id;
        event_type = event;
        poster_avail = poster;
        uri = u;
        postData = jsonObject;
        mcontext = context;
        fragmentManager = fm;
    }

    /*Call makehttpresponse function and get json response and save it to shared Preference */

    @Override
    protected String doInBackground(URL... urls) {
        URL url = urls[0];

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        df = new LoadDialog();
        df.show(fragmentManager,"Wait");
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("Problem", "Problem making the HTTP request.", e);
        }
        if (!TextUtils.isEmpty(jsonResponse)) {
            SharedPreferences json = mcontext.getSharedPreferences("JSON", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = json.edit();
            editor.putString("JSONresp", jsonResponse);
            editor.commit();
        }
        return jsonResponse;
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (postData != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                writer.write(String.valueOf(postData));
                writer.flush();
            }

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("Error", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("Error", "Problem Uploading Event.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /*Check whether jsonresponse is empty to show success and failure toast*/


    @Override
    protected void onPostExecute(String jsonResponse) {
        df.dismiss();
        if (!TextUtils.isEmpty(jsonResponse)) {
            Toast.makeText(mcontext, "Event list updated successfully", Toast.LENGTH_SHORT).show();
            if (poster_avail.equals("true")) {
                StorageReference sr = storageReference.child("poster").child(event_type).child(ID);
                UploadTask uploadTask = sr.putFile(uri);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Toast.makeText(mcontext, "Poster Uploaded", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mcontext, "Poster Upload failed", Toast.LENGTH_SHORT).show();

                    }
                });
            }

        } else {
            Toast.makeText(mcontext, "Error in Adding", Toast.LENGTH_SHORT).show();
        }
    }


}
