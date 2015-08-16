package com.example.franktastic4.mylifts.CalendarPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.example.franktastic4.mylifts.AnalysisPackage.AnalysisActivity;
import com.example.franktastic4.mylifts.JournalPackage.JournalActivity;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementsActivity;
import com.example.franktastic4.mylifts.NavigationDrawerFragment;
import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.SettingsPackage.SettingsActivity;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutList;

/**
 * Created by Franktastic4 on 7/5/15.
 */


public class CalendarActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    public NavigationDrawerFragment mNavigationDrawerFragment;
    public CalendarFragment myCalendarFrag;
    public String savedTitle;
    int selectCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_calendar);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer_calendar,
                (DrawerLayout) findViewById(R.id.drawer_layout_calendar));

        mNavigationDrawerFragment.updateSelectedItem(3);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.global, menu);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Analysis");
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        //Defaults to 0 after we start this activity
        if(selectCounter == 0){

            FragmentManager fragmentManager = getSupportFragmentManager();
            myCalendarFrag = new CalendarFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_calendar, myCalendarFrag, "calendar_fragment")
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

            }else if(position == 4){
                Intent intent = new Intent(this, AnalysisActivity.class);
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

    public void onDrawerOpenedCallback(){

        if(getSupportActionBar().getTitle() != null) {
            savedTitle = getSupportActionBar().getTitle().toString();
            getSupportActionBar().setTitle("MyLifts");
        }

    }

    @Override
    public void onBackPressed() {
        if(mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.returnDrawerLayout().closeDrawers();
        }else {
            super.onBackPressed();
        }
    }

    public void setActionBarTitle(String newTitle){
        getSupportActionBar().setTitle(newTitle);
        savedTitle = newTitle;
    }


}
