package com.example.acer.vitevents.AdapterClass;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.acer.vitevents.ConferenceFragment;
import com.example.acer.vitevents.SeminarFragment;
import com.example.acer.vitevents.WorkshopFragment;

/**
 * This is a custom adapter class for ViewPager
 * getItem function returns fragment according to selected position
 * getPageTitle returns title of the selected fragments
 */

public class CategoryAdapter extends FragmentPagerAdapter {

    public CategoryAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new SeminarFragment();
        } else if (position == 1) {
            return new WorkshopFragment();
        } else {
            return new ConferenceFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Seminar";
        } else if (position == 1) {
            return "Workshop";
        } else {
            return "Conference";
        }
    }
}
