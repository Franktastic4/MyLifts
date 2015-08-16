package com.example.franktastic4.mylifts.AnalysisPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

import com.example.franktastic4.mylifts.CalendarPackage.CalendarActivity;
import com.example.franktastic4.mylifts.JournalPackage.JournalActivity;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementsActivity;
import com.example.franktastic4.mylifts.NavigationDrawerFragment;
import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.SettingsPackage.SettingsActivity;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutList;

/**
 * Created by Franktastic4 on 7/26/15.
 */
public class AnalysisActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    public String savedTitle;
    public NavigationDrawerFragment mNavigationDrawerFragment;
    int selectCounter = 0;
    AnalysisFragment myAnalysisFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_activity);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_analysis);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer_analysis,
                (DrawerLayout) findViewById(R.id.drawer_layout_analysis));

        mNavigationDrawerFragment.updateSelectedItem(4);

    }

    public void onNavigationDrawerItemSelected(int position) {

        //Defaults to 0 after we start this activity
        if(selectCounter == 0){

            FragmentManager fragmentManager = getSupportFragmentManager();
            myAnalysisFragment = new AnalysisFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_analysis, myAnalysisFragment, "analysis_fragment")
                    .commit();
            selectCounter++;

        }else{

            if(position == 0) {
                Intent intent = new Intent(this, WorkoutList.class);
                startActivity(intent);
                mNavigationDrawerFragment.updateSelectedItem(position);
                finish();
            }else if(position == 1){
                Intent intent = new Intent(this, MeasurementsActivity.class);
                startActivity(intent);
                mNavigationDrawerFragment.updateSelectedItem(position);
                finish();

            }else if(position == 2){
                Intent intent = new Intent(this, JournalActivity.class);
                startActivity(intent);
                mNavigationDrawerFragment.updateSelectedItem(position);
                finish();

            }else if(position == 3){
                Intent intent = new Intent(this, CalendarActivity.class);
                startActivity(intent);
                mNavigationDrawerFragment.updateSelectedItem(position);
                finish();

            }else if(position == 5){
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                mNavigationDrawerFragment.updateSelectedItem(position);
                finish();
            }

        }


    }

    @Override
    public void onDrawerClosedCallback(){

        if(savedTitle != null) {
            getSupportActionBar().setTitle(savedTitle);
        }else{
            getSupportActionBar().setTitle("NULL");
        }

    }

    @Override
    public void onBackPressed() {
        //closes the navigation drawer before it closes out of the app, or moves back a fragment.

        if(mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.returnDrawerLayout().closeDrawers();
            onDrawerClosedCallback();

        }else {
            super.onBackPressed();
        }
    }

    public void onDrawerOpenedCallback(){

        if(getSupportActionBar().getTitle() != null) {
            savedTitle = getSupportActionBar().getTitle().toString();
            getSupportActionBar().setTitle("MyLifts");
        }

    }

    public void setActionBarTitle(String newTitle){
        getSupportActionBar().setTitle(newTitle);
        savedTitle = newTitle;
    }
}
