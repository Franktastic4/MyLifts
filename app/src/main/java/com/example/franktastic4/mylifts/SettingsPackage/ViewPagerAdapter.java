package com.example.franktastic4.mylifts.SettingsPackage;

/**
 * Created by Franktastic4 on 7/16/15.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter{

    private String[] tabsTitles;

    public ViewPagerAdapter(FragmentManager fm, String[] tabsTitles) {
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
                return new FeaturesFragment();
            case 1:
                return new ExportFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return tabsTitles.length;
    }
}
