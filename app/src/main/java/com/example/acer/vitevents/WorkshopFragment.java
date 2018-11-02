package com.example.acer.vitevents;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.acer.vitevents.AdapterClass.WorkAdminAdapter;
import com.example.acer.vitevents.AdapterClass.WorkshopAdapter;
import com.example.acer.vitevents.AsyncTasks.EventAsyncTask;
import com.example.acer.vitevents.WordClass.WorkshopWord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import static com.example.acer.vitevents.AddEvent.LOG_TAG;

/**
 *  Workshop Fragment Creates Ui according to the login Type
 * Eg., WorkAdminAdapter is used when Admin logs and WorkshopAdapter is used when Student logs
 * SharedPreferenceChangeListener is used and whenever the preference is changed create() method is called
 so the UI gets Updated According to the Changed Pref data
 * The create() method also gets invoked whenever the fragments life cycle enters OnResume part
 */

public class WorkshopFragment extends Fragment {

    private String From;
    private SwipeRefreshLayout refreshLayout;
    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.list, container, false);
        Bundle b = getActivity().getIntent().getExtras();
        From = b.getString("From");
        refreshLayout = v.findViewById(R.id.refresh);

        /*function call to update ui based to the data from pref*/

        create();

        /* Function when Refresh is done*/

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventAsyncTask eventAsyncTask = new EventAsyncTask();
                eventAsyncTask.setContext(getContext(), getFragmentManager());
                eventAsyncTask.execute();
                refreshLayout.setRefreshing(false);
            }
        });
        SharedPreferences shf = getContext().getSharedPreferences("JSON", Context.MODE_PRIVATE);

        /*Update ui every time preference gets changed*/

        shf.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                create();
            }
        });
        return v;
    }

    /*Update ui onResume*/

    @Override
    public void onResume() {
        create();
        super.onResume();
    }

    /*function to update ui with the data from preference*/


    private void create() {
        SharedPreferences shf = getContext().getSharedPreferences("JSON", Context.MODE_PRIVATE);
        String defJson = "{\"seminar\":[],\"workshop\":[],\"conference\":[]}";
        String jsonresp = shf.getString("JSONresp", defJson);
        ArrayList<WorkshopWord> workshop = extractFeatureFromJson(jsonresp);
        if (From.equals("Student")) {
            updateStudentUi(workshop);
        } else if (From.equals("Admin")) {
            updateAdminUi(workshop);
        }
    }

    /*function to update Admin view*/


    private void updateAdminUi(ArrayList<WorkshopWord> workshopWords) {
        WorkAdminAdapter wordAdapter = new WorkAdminAdapter(getActivity(), workshopWords, getFragmentManager());
        final ListView listView = v.findViewById(R.id.list);
        listView.setAdapter(wordAdapter);
        wordAdapter.notifyDataSetChanged();
    }

    /*function to update Student view*/


    private void updateStudentUi(final ArrayList<WorkshopWord> workshopWords) {
        WorkshopAdapter wordAdapter = new WorkshopAdapter(getActivity(), workshopWords, getFragmentManager());
        final ListView listView =v.findViewById(R.id.list);
        listView.setAdapter(wordAdapter);
    }

    /*Function to extract event details from json data*/

    private ArrayList<WorkshopWord> extractFeatureFromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        ArrayList<WorkshopWord> w = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray workshop = baseJsonResponse.getJSONArray("workshop");

            for (int i = 0; i < workshop.length(); i++) {
                JSONObject wor = workshop.getJSONObject(i);
                String id = wor.getString("id");
                String Title = wor.getString("title");
                String Venue = wor.getString("venue");
                String Millisec = wor.getString("millisec");
                String Org = wor.getString("Org");
                String Contact = wor.getString("contact");
                String Club = wor.getString("club");
                String Link = wor.getString("link");
                String Des = wor.getString("des");
                String fees = wor.getString("fees");
                String poster = wor.getString("poster");
                w.add(new WorkshopWord(id, Title, Venue, Millisec, Org, Club, Link, Des, fees, Contact, poster));
            }
            return w;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }


}
