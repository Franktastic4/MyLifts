package com.example.franktastic4.mylifts.AnalysisPackage;

/**
 * Created by Franktastic4 on 7/27/15.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Franktastic4 on 7/16/15.
 */

public class ViewPagerAdapterAnalysis extends FragmentPagerAdapter {

    private String[] tabsTitles;

    public ViewPagerAdapterAnalysis(FragmentManager fm, String[] tabsTitles) {
        super(fm);
        this.tabsTitles = tabsTitles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabsTitles[position];
    }

    @Override
    public Fragment getItem(int index) {
        switch(index) {
            case 0:
                return new WorkoutAnalysisFragment();
            case 1:
                return new MeasurementAnalysisFragment();
            case 2:
                return new StatsAnalysisFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabsTitles.length;
    }
}

