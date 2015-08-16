package com.example.franktastic4.mylifts.SettingsPackage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutList;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableDbHelper;

/**
 * Created by Franktastic4 on 7/5/15.
 */


public class SettingsActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    public NavigationDrawerFragment mNavigationDrawerFragment;
    public SettingsFragment mySettingsFragment;
    public String savedTitle;
    int selectCounter = 0;

    WorkoutTableDbHelper mDbHelper;
    SQLiteDatabase WorkoutTableDb;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_settings);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer_settings,
                (DrawerLayout) findViewById(R.id.drawer_layout_settings));

        mNavigationDrawerFragment.updateSelectedItem(5);

        mDbHelper = new WorkoutTableDbHelper(getApplicationContext());
        WorkoutTableDb = mDbHelper.getWritableDatabase();

        sharedPref = getSharedPreferences(
                getString(R.string.GoalViewKey), Context.MODE_PRIVATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.global, menu);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Settings");
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        //Defaults to 0 after we start this activity
        if(selectCounter == 0){

            FragmentManager fragmentManager = getSupportFragmentManager();
            mySettingsFragment = new SettingsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_settings, mySettingsFragment, "settings_fragment_tag")
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
                Intent intent = new Intent(this, com.example.franktastic4.mylifts.CalendarPackage.CalendarActivity.class);
                startActivity(intent);
                mNavigationDrawerFragment.updateSelectedItem(position);
                finish();
            }else if(position == 4){
                Intent intent = new Intent(this, AnalysisActivity.class);
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
