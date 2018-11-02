package com.example.acer.vitevents.AsyncTasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.acer.vitevents.Dialogs.LoadDialog;
import com.example.acer.vitevents.ViewEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * DeleteAsyncTask deletes events to the server and deletes their poster from the firebase Storage
 * The AsyncTask is invoked from the Delete Dialog
 * The path file for FireBase storage is /posters/event_type/event_ID
 */

public class DeleteAsyncTask extends AsyncTask<Void,Void,Void> {
    private String LOG_TAG = ViewEvent.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private Context mcontext;
    private String jsonResponse = "";
    private String From;
    private JSONObject postData;
    private String URL;
    private String ID;
    private DialogFragment df;
    private FragmentManager fragmentManager;


    public void setContext(Context mc,String fr,String id,FragmentManager fm){
        mcontext= mc;
        From = fr;
        ID = id;
        fragmentManager = fm;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        postData = new JSONObject();
        try {
            postData.put("id",ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (From) {
            case "Workshop":
                URL = "https://6b39f94b.ngrok.io/deleteworkshop";
                break;
            case "Conference":
                URL = "https://6b39f94b.ngrok.io/deleteconference";
                break;
            case "Seminar":
                URL = "https://6b39f94b.ngrok.io/deleteseminar";
                break;
        }

        df = new LoadDialog();
        df.show(fragmentManager,"Wait..");
        URL url = createUrl(URL);

        // Perform HTTP request to the URL and receive a JSON response back
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        if(!TextUtils.isEmpty(jsonResponse)){
            SharedPreferences json = mcontext.getSharedPreferences("JSON", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = json.edit();
            editor.putString("JSONresp",jsonResponse);
            editor.commit();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(!TextUtils.isEmpty(jsonResponse)){
            Toast.makeText(mcontext,"Deleted Successfully",Toast.LENGTH_SHORT).show();
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            StorageReference sr = storageReference.child("poster").child(From).child(ID);
            sr.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(mcontext,"Poster deleted successfully",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mcontext,"Poster not deleted",Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(mcontext,"Error in Deleting",Toast.LENGTH_SHORT).show();

        }

        df.dismiss();
    }

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
            urlConnection.setRequestMethod("DELETE");
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
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem Adding new Event.", e);
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

}
