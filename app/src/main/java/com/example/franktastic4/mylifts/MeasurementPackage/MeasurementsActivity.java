package com.example.franktastic4.mylifts.MeasurementPackage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.franktastic4.mylifts.AnalysisPackage.AnalysisActivity;
import com.example.franktastic4.mylifts.JournalPackage.JournalActivity;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableReaderContract.MeasurementTable;
import com.example.franktastic4.mylifts.NavigationDrawerFragment;
import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.SettingsPackage.SettingsActivity;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutList;

import java.util.Calendar;


public class MeasurementsActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static NavigationDrawerFragment mNavigationDrawerFragment;
    public ActionBar actionBar;
    public MeasurementsFragment myMeasurementsFragment;
    private int selectCounter = 0;
    public static boolean mMeasurementActivityIsFront = false;
    public String savedTitle;
    public int savedTitleCounter = 0;

    public static ActionMenuItemView plusButton;
    public static ActionMenuItemView minusButton;
    public static ActionMenuItemView undoTappedButton;

    MeasurementTableDbHelper mDbHelper;
    SQLiteDatabase MeasurementTableDB;

    SharedPreferences sharedPref;

    public void setActionBarTitle(String newTitle){
        getSupportActionBar().setTitle(newTitle);
        savedTitle = newTitle;
    }

    @Override
    public void onResume(){
        mMeasurementActivityIsFront = true;
        super.onResume();

    }

    @Override
    public void onPause(){
        mMeasurementActivityIsFront = false;
        super.onPause();
    }

    public void printDatabase() {

        SQLiteDatabase MeasurementTableReadable = new MeasurementTableDbHelper(getApplicationContext()).getReadableDatabase();

        String[] myProjection = {
                MeasurementTable.MEASUREMENT,
                MeasurementTable.MEASUREMENT_VALUE,
                MeasurementTable.CALENDAR,
                MeasurementTable.GOAL_START
        };

        Cursor tempCur = MeasurementTableReadable.query(
                MeasurementTable.TABLE_NAME,
                myProjection,
                null,
                null,
                null,
                null,
                null);

        if(tempCur.moveToFirst()){


        }

        while (tempCur.moveToNext()) {

            Log.d("TABLE ITEM", tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(MeasurementTable.GOAL_START))
            );

        }
    }

    public void addMeasurementDone(View view){

        if(MeasurementsActivity.mMeasurementActivityIsFront) {
            NavigationDrawerFragment.plusButton.setVisibility(View.VISIBLE);
            NavigationDrawerFragment.minusButton.setVisibility(View.VISIBLE);
            NavigationDrawerFragment.undoTappedButton.setVisibility(View.GONE);
        }

        EditText myEditText = (EditText) findViewById(R.id.add_measurement_edit_text);

        if(myEditText.getText().toString().equals("")) {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
            getSupportFragmentManager().popBackStack();

        }else{

            Calendar myCalendar = Calendar.getInstance();
            MeasurementInstance myMeasurementObject = MeasurementInstance.newInstance(myEditText.getText().toString());

            myMeasurementObject.setMeasurementInstanceName(myEditText.getText().toString());
            myMeasurementObject.setDate(myCalendar);
            myMeasurementsFragment.measurementInstanceArray.add(myMeasurementObject);

            //Add to database
            ContentValues values = new ContentValues();
            values.put(MeasurementTable.MEASUREMENT  , myEditText.getText().toString());
            values.put(MeasurementTable.CALENDAR, "1");
            MeasurementTableDB.insert(MeasurementTable.TABLE_NAME, null, values);
            printDatabase();

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);

            getSupportFragmentManager().popBackStack();
            myMeasurementsFragment.myMeasureInstanceArrayAdapter.notifyDataSetChanged();
            myMeasurementsFragment.myMeasurementFragmentListView.invalidate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);
        mMeasurementActivityIsFront = true;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_measurements);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer_measurements,
                (DrawerLayout) findViewById(R.id.drawer_layout_measurements));

        mNavigationDrawerFragment.updateSelectedItem(1);

        mDbHelper = new MeasurementTableDbHelper(getApplicationContext());
        MeasurementTableDB = mDbHelper.getWritableDatabase();

        sharedPref = getSharedPreferences(
                getString(R.string.GoalViewKey), Context.MODE_PRIVATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.menu_measurements, menu);
            actionBar = getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Measurements");
            return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        //Defaults to 0 after we start this activity
        if(selectCounter == 0){

            FragmentManager fragmentManager = getSupportFragmentManager();
            myMeasurementsFragment = myMeasurementsFragment.newInstance(position + 1);
            fragmentManager.beginTransaction()
                    .replace(R.id.container_measurements, myMeasurementsFragment, "measurements_fragment_tag")
                    .commit();
            selectCounter++;

        }else{

           if(position == 0) {
               Intent intent = new Intent(this, WorkoutList.class);
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
        //closes the navigation drawer before it closes out of the app, or moves back a fragment.

        if(mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.returnDrawerLayout().closeDrawers();
            restoreButtons();

        }else {
            super.onBackPressed();
        }
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
