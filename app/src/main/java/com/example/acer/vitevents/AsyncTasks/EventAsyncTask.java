package com.example.acer.vitevents.AsyncTasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.acer.vitevents.Dialogs.LoadDialog;
import com.example.acer.vitevents.ViewEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;


public class EventAsyncTask extends AsyncTask<Void,Void,Void> {

    private String LOG_TAG = ViewEvent.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private Context mcontext;
    private String jsonResponse = "";
    private DialogFragment df;
    private FragmentManager fragmentManager;

    public void setContext(Context context,FragmentManager fm) {
        mcontext = context;
        fragmentManager = fm;
    }
    @Override

    /*Call makehttpresponse function and get json response and save it to shared Preference */

    protected Void doInBackground(Void... voids) {

        df = new LoadDialog();
        df.show(fragmentManager,"Wait..");

        String URL="https://6b39f94b.ngrok.io/events";

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

    /*Check whether jsonresponse is empty to show success and failure toast*/

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(!TextUtils.isEmpty(jsonResponse)){
            Toast.makeText(mcontext,"Events Updated successfully",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(mcontext,"Error in Updating",Toast.LENGTH_SHORT).show();

        }
        df.dismiss();
    }

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
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
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
