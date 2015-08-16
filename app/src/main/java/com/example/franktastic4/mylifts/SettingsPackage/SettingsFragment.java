package com.example.franktastic4.mylifts.SettingsPackage;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.franktastic4.mylifts.R;

/**
 * Created by Franktastic4 on 7/16/15.
 */
public class SettingsFragment extends android.support.v4.app.Fragment {
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    // Tabs titles
    private String[] tabsTitles = {"Features", "Export"};


    public void SettingsFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        ((SettingsActivity)getActivity()).getSupportActionBar().setTitle("Settings");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sliding_bar_and_view_pager_layout, container, false);

        // ViewPager
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), tabsTitles));

        // Sliding tab layout
        mSlidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);


        return rootView;
    }
}