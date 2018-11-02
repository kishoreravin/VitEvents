package com.example.acer.vitevents;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acer.vitevents.AdapterClass.CategoryAdapter;
import com.example.acer.vitevents.AsyncTasks.EventAsyncTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewEvent activity has ViewPager which uses category Adapter to set Fragments in the view
 * This Activity Layout Contains Floating Action Buttons to Add event in admin view alone and email id ,about,Sign out functions in both
        student and admin view
 *EventAsyncTask is executed in this Activity which gets json data from the server and adds the json data to the sharedPreference
 */

public class ViewEvent extends AppCompatActivity {

    private FloatingActionButton fab;
    private FloatingActionButton About;
    private FloatingActionButton EmailId;
    private LinearLayout fabLayout1, fabLayout2, fabLayout3, fabLayout4;
    private View fabBGLayout;
    private boolean isFABOpen = false;
    private String From;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewevent);
        Bundle b = getIntent().getExtras();
        From = b.getString("From");
        ViewPager viewPager =  findViewById(R.id.viewpager);

        CategoryAdapter adapter = new CategoryAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        TabLayout tabLayout =  findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

        fabLayout1 =  findViewById(R.id.fabLayout1);
        fabLayout2 =  findViewById(R.id.fabLayout2);
        fabLayout3 =  findViewById(R.id.fabLayout3);
        fabLayout4 =  findViewById(R.id.fabLayout4);
        fab =  findViewById(R.id.fab);
        About =  findViewById(R.id.About);
        FloatingActionButton signOut =  findViewById(R.id.SignOut);
        EmailId =  findViewById(R.id.EmailId);
        FloatingActionButton addEvent =  findViewById(R.id.AddEvent);
        fabBGLayout = findViewById(R.id.fabBGLayout);
        TextView userid =  findViewById(R.id.user_email);

        FirebaseUser  user = firebaseAuth.getCurrentUser();
        String email = user.getEmail();

        userid.setText(email);
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(),AddEvent.class));
            }
        });

        /*Main Floating Action Button to show other FAB buttons*/

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        /*Floating Action button to Signout the current user*/

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getBaseContext(),LoginActivity.class));
            }
        });

        /*Function to hide Floating Action Button to when Background layout is clicked*/

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

        /*Check whether network is available and call Async task to download the json details*/

        if (isNetworkAvailable()) {
            EventAsyncTask eventAsyncTask = new EventAsyncTask();
            eventAsyncTask.setContext(getBaseContext(), getSupportFragmentManager());
            eventAsyncTask.execute();
        } else {
            Toast.makeText(this, "Connect to internet to get recent events", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo active = cm.getActiveNetworkInfo();
        return active != null && active.isConnected();

    }

    private void showFABMenu() {
        isFABOpen = true;
        fabBGLayout.setVisibility(View.VISIBLE);
        fab.animate().rotationBy(180);
        fabLayout1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout1.setVisibility(View.VISIBLE);
        fabLayout2.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout2.setVisibility(View.VISIBLE);
        fabLayout3.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
        fabLayout3.setVisibility(View.VISIBLE);
        if (!From.equals("Student")) {
            fabLayout4.animate().translationY(-getResources().getDimension(R.dimen.standard_190));
            fabLayout4.setVisibility(View.VISIBLE);
        }

    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-180);
        fabLayout1.animate().translationY(0);
        fabLayout2.animate().translationY(0);
        fabLayout4.animate().translationY(0);
        fabLayout3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout1.setVisibility(View.GONE);
                    fabLayout2.setVisibility(View.GONE);
                    fabLayout3.setVisibility(View.GONE);
                    fabLayout4.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /*Function to hide Floating Action Button to when Back button is clicked*/


    @Override
    public void onBackPressed() {
        if (isFABOpen) {
            closeFABMenu();
        } else {
            super.onBackPressed();
        }
    }
}

