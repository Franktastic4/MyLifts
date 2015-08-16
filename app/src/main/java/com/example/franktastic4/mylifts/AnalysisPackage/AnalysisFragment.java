package com.example.franktastic4.mylifts.AnalysisPackage;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.SettingsPackage.SlidingTabLayout;

/**
 * Created by Franktastic4 on 7/27/15.
 */
public class AnalysisFragment extends android.support.v4.app.Fragment {

    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    // Tabs titles
    private String[] tabsTitles = {"Workouts", "Measurements", "Stats"};


    public void AnalysisFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        ((AnalysisActivity)getActivity()).getSupportActionBar().setTitle("Analysis");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sliding_bar_and_view_pager_layout, container, false);

        // ViewPager
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new ViewPagerAdapterAnalysis(getChildFragmentManager(), tabsTitles));

        // Sliding tab layout
        mSlidingTabLayout = (SlidingTabLayout) rootView.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);


        return rootView;
    }

}
