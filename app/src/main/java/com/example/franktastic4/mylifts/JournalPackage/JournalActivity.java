package com.example.franktastic4.mylifts.JournalPackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.franktastic4.mylifts.AnalysisPackage.AnalysisActivity;
import com.example.franktastic4.mylifts.CalendarPackage.CalendarActivity;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementsActivity;
import com.example.franktastic4.mylifts.NavigationDrawerFragment;
import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.SettingsPackage.SettingsActivity;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutList;

import java.util.Calendar;


public class JournalActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static NavigationDrawerFragment mNavigationDrawerFragment;
    public ActionBar actionBar;
    public JournalFragment myJournalFragment;
    private int selectCounter = 0;
    public static ActionMenuItemView plusButton;
    public static ActionMenuItemView minusButton;
    public static ActionMenuItemView undoTappedButton;
    public String savedTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_journal);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer_journal,
                (DrawerLayout) findViewById(R.id.drawer_layout_journal));

        mNavigationDrawerFragment.updateSelectedItem(2);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Journal");


        //When this activity is started by Calendar...
        Intent myIntent = getIntent();
        if(myIntent.getBooleanExtra("calendarToJournalBool",false)){

            FragmentManager fragmentManager = getSupportFragmentManager();
            myJournalFragment = new JournalFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_journal, myJournalFragment, "journal_fragment_tag")
                            //.addToBackStack("Workout List Fragment") Took this one out, so you can't go any further back.
                    .commit();

            Calendar myTempCal = Calendar.getInstance();
            myTempCal.set(Calendar.YEAR, myIntent.getIntExtra("yearKey", 0));
            myTempCal.set(Calendar.MONTH, myIntent.getIntExtra("monthKey", 0));
            myTempCal.set(Calendar.DAY_OF_MONTH, myIntent.getIntExtra("dayOfMonthKey", 0));
            myJournalFragment.setCalendar(myTempCal);
            myJournalFragment.setFromCalenderTrue(true);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.menu_journal, menu);
            actionBar.setDisplayShowTitleEnabled(true);
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        //Defaults to 0 after we start this activity
        if(selectCounter == 0){
            FragmentManager fragmentManager = getSupportFragmentManager();
            myJournalFragment = new JournalFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_journal, myJournalFragment, "journal_fragment_tag")
                            //.addToBackStack("Workout List Fragment") Took this one out, so you can't go any further back.
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
            }else if(position == 3){
                Intent intent = new Intent(this, CalendarActivity.class);
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
    public void onDrawerClosedCallback(){//Here to satisfy callback
    }

    public void onDrawerOpenedCallback(){}

    @Override
    public void onBackPressed() {
        //closes the navigation drawer before it closes out of the app, or moves back a fragment.

        if(mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.returnDrawerLayout().closeDrawers();

            getSupportActionBar().setTitle("Journal");
            restoreButtons();

        }else {
            super.onBackPressed();
        }
    }

    public void setActionBarTitle(String newTitle){
        getSupportActionBar().setTitle(newTitle);
        savedTitle = newTitle;
    }

    public void hideButtons(){


        plusButton = (ActionMenuItemView)findViewById(R.id.action_example);
        minusButton = (ActionMenuItemView)findViewById(R.id.undo);
        undoTappedButton = (ActionMenuItemView)findViewById(R.id.undo_tapped);

        plusButton.setVisibility(View.VISIBLE);
        minusButton.setVisibility(View.VISIBLE);
        undoTappedButton.setVisibility(View.GONE);

        invalidateOptionsMenu();

    }

    public void restoreButtons(){

        plusButton = (ActionMenuItemView)findViewById(R.id.action_example);
        minusButton = (ActionMenuItemView)findViewById(R.id.undo);
        undoTappedButton = (ActionMenuItemView)findViewById(R.id.undo_tapped);

        plusButton.setVisibility(View.GONE);
        minusButton.setVisibility(View.GONE);
        undoTappedButton.setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
