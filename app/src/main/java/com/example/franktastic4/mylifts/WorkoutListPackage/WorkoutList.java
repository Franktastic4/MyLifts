package com.example.franktastic4.mylifts.WorkoutListPackage;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.franktastic4.mylifts.AnalysisPackage.AnalysisActivity;
import com.example.franktastic4.mylifts.JournalPackage.JournalActivity;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementsActivity;
import com.example.franktastic4.mylifts.NavigationDrawerFragment;
import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.SettingsPackage.SettingsActivity;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableReaderContract.WorkoutTable;

import java.util.ArrayList;
import java.util.Calendar;


public class WorkoutList extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {


    private NavigationDrawerFragment mNavigationDrawerFragment;
    //private CharSequence mTitle;
    public Workout_List_Fragment mWorkoutListFragment;
    public ActionBar actionBar;
    public static int lastPosition = 0;
    public static ExerciseList mExerciseListFragment;
    public static int lastWorkoutSelected;
    public static boolean mActivityIsFront = true;
    private int savedTitleCounter = 0;
    private String savedTitle;
    private int selectCounter = 0;

    ActionMenuItemView plusButton;
    ActionMenuItemView minusButton;
    ActionMenuItemView undoTappedButton;

    //Database stuff
    WorkoutTableDbHelper mDbHelper;
    static SQLiteDatabase WorkoutTableDb;

    public static void deleteFromDatabase(String deleteWorkoutName){
        //WorkoutTableDb.delete(WorkoutTable.TABLE_NAME, WorkoutTable.WORKOUT_NAME + "=" + deleteWorkoutName, null);

        String whereClause = WorkoutTable.WORKOUT_NAME + "=?";
        String[] whereArgs = new String[] {deleteWorkoutName };
        WorkoutTableDb.delete(WorkoutTable.TABLE_NAME, whereClause, whereArgs);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workout_list);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        actionBar = getSupportActionBar();


        //Database stuff
        mDbHelper = new WorkoutTableDbHelper(getApplicationContext());
        WorkoutTableDb = mDbHelper.getWritableDatabase();

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        if(selectCounter == 0){
            getSupportActionBar().setTitle("Workouts");
            FragmentManager fragmentManager = getSupportFragmentManager();
            mWorkoutListFragment = Workout_List_Fragment.newInstance(position + 1);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mWorkoutListFragment, "workoutList_fragment_tag")
                    //.addToBackStack("Workout List Fragment") //Took this one out, so you can't go any further back.
                    .commit();
            selectCounter++;

        }else {

            if (position == 1) {
                Intent intent = new Intent(this, MeasurementsActivity.class);
                getSupportActionBar().setTitle("Measurements");
                startActivity(intent);
                mNavigationDrawerFragment.updateSelectedItem(position);
                finish();

            } else if (position == 2) {
                Intent intent = new Intent(this, JournalActivity.class);
                getSupportActionBar().setTitle("Journal");
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
        //restoreButtons();

    }

    @Override
    public void onDrawerOpenedCallback(){

        if(getSupportActionBar().getTitle() != null) {
            savedTitle = getSupportActionBar().getTitle().toString();
            getSupportActionBar().setTitle("MyLifts");
        }
        //hideButtons();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.global, menu);

            actionBar = getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            //hideButtons();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {   mActivityIsFront = true;
        super.onResume();

        //Lets fragment deal with title onResume, and onPause


    }

    @Override
    public void onPause(){
        mActivityIsFront = false;
        super.onPause();


    }

    @Override
    public void onBackPressed() {
        //closes the navigation drawer before it closes out of the app, or moves back a fragment.

        if(mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.returnDrawerLayout().closeDrawers();

            //set name of actionbar back
            getSupportActionBar().setTitle(R.string.WorkoutFragmentString);
            //restoreButtons();


        }else {
            super.onBackPressed();
        }
    }

    public void hideButtons(){

        plusButton = (ActionMenuItemView)findViewById(R.id.action_example);
        minusButton = (ActionMenuItemView)findViewById(R.id.undo);
        undoTappedButton = (ActionMenuItemView)findViewById(R.id.undo_tapped);

        plusButton.setVisibility(View.GONE);
        minusButton.setVisibility(View.GONE);
        undoTappedButton.setVisibility(View.GONE);

    }

    public void restoreButtons(){

        plusButton = (ActionMenuItemView)findViewById(R.id.action_example);
        minusButton = (ActionMenuItemView)findViewById(R.id.undo);
        undoTappedButton = (ActionMenuItemView)findViewById(R.id.undo_tapped);

        plusButton.setVisibility(View.VISIBLE);
        minusButton.setVisibility(View.VISIBLE);
        undoTappedButton.setVisibility(View.GONE);

    }

    public void setActionBarTitle(String newTitle){
        getSupportActionBar().setTitle(newTitle);
        savedTitle = newTitle;
    }

    public void addWorkoutDone(View view){

        restoreButtons();

        //set up handle on the workout name variable
        EditText new_workout_field;
        new_workout_field = (EditText) findViewById(R.id.add_workout_message);

        if(!new_workout_field.getText().toString().equals("")) {

            //set up workout object
            mExerciseListFragment = ExerciseList.newInstance(new_workout_field.getText().toString());

            //add workout object to list of workouts
            mWorkoutListFragment.WorkoutNames.add(mExerciseListFragment);

            ContentValues values = new ContentValues();
            values.put(WorkoutTable.WORKOUT_NAME, new_workout_field.getText().toString());
            values.put(WorkoutTable.DATE, "1");

            Spinner spinner = (Spinner) findViewById(R.id.spinner1);
            String mySpinnerValue = spinner.getSelectedItem().toString();

            if(!mySpinnerValue.equals("")){
                //Set the value of the workout
                values.put(WorkoutTable.DAYSELECTED, mySpinnerValue);
            }

            WorkoutTableDb.insert(WorkoutTable.TABLE_NAME, null, values);
        }


        //is a problem on my actual phone -- Try this.
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(new_workout_field.getWindowToken(), 0);

        getSupportFragmentManager().popBackStack();
        mWorkoutListFragment.workoutObjectArrayAdapter.notifyDataSetChanged();
        mWorkoutListFragment.lvFragmentWorkoutList.invalidate();


    }

    public void printDatabase() {

        SQLiteDatabase WorkoutTableReadable = new WorkoutTableDbHelper (getApplicationContext()).getReadableDatabase();

        String[] myProjection = {
                WorkoutTable.WORKOUT_NAME,
                WorkoutTable.EXERCISE_NAME,
                WorkoutTable.DATE,
                WorkoutTable.SET,
                WorkoutTable.REPS,
                WorkoutTable.WEIGHT
        };

        Cursor tempCur = WorkoutTableReadable.query(
                WorkoutTable.TABLE_NAME,
                myProjection,
                null,
                null,
                null,
                null,
                null);

        tempCur.moveToFirst();
        while (tempCur.moveToNext()) {

            Log.d("TABLE ITEM", tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.EXERCISE_NAME))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.SET))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.REPS))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WEIGHT))
            );

        }
    }

    public static class Workout_List_Fragment extends android.support.v4.app.Fragment {

        public ArrayList<ExerciseList> WorkoutNames = new ArrayList<ExerciseList>();
        public ListView lvFragmentWorkoutList;
        int addOnlyOnce = 0;
        public WorkoutObjectArrayAdapter workoutObjectArrayAdapter;
        private static final String ARG_SECTION_NUMBER = "section_number";
        public int firstWorkoutListCounter = 0;
        public String savedTitle;
        private boolean firstRunOfDay = true;

        WorkoutTableDbHelper mDbHelper;
        SQLiteDatabase WorkoutTableDb;
        SQLiteDatabase WorkoutTableReadable;

        //private NavigationDrawerFragment mNavigationDrawerFragment;

        public static Workout_List_Fragment newInstance(int sectionNumber) {
            Workout_List_Fragment fragment = new Workout_List_Fragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
         //  mNavigationDrawerFragment = (NavigationDrawerFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

            //Database stuff
            mDbHelper = new WorkoutTableDbHelper(getActivity().getApplicationContext());
            WorkoutTableDb = mDbHelper.getWritableDatabase();
            WorkoutTableReadable = mDbHelper.getReadableDatabase();

        }

        public Workout_List_Fragment() {}

        @Override
        public void onResume() {
            super.onResume();

            ((WorkoutList) getActivity()).setActionBarTitle("Workouts");
            setUndoBooleanFalse();

            if(WorkoutNames.isEmpty()){
                populateWorkoutNames();
            }

            //printDatabase();
        }

        public void printDatabase() {

            String[] myProjection = {
                    WorkoutTable.WORKOUT_NAME,
                    WorkoutTable.EXERCISE_NAME,
                    WorkoutTable.DATE,
                    WorkoutTable.SET,
                    WorkoutTable.REPS,
                    WorkoutTable.WEIGHT,
                    WorkoutTable.DAYSELECTED
            };

            Cursor tempCur = WorkoutTableReadable.query(
                    WorkoutTable.TABLE_NAME,
                    myProjection,
                    null,
                    null,
                    null,
                    null,
                    null);

            tempCur.moveToFirst();
            while (tempCur.moveToNext()) {

                Log.d("TABLE ITEM", tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME))
                                + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.EXERCISE_NAME))
                                + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE))
                                + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.SET))
                                + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.REPS))
                                + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WEIGHT))
                                + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED))
                );

            }
        }

        public void setUndoBooleanFalse(){

            NavigationDrawerFragment.mUndoWorkoutListEnabled = false;
            NavigationDrawerFragment.mUndoExerciseListEnabled = false;
            NavigationDrawerFragment.mUndoExerciseInstanceEnabled = false;

        }

        public void populateWorkoutNames(){

            String[] myProjection = {
                    WorkoutTable.WORKOUT_NAME,
                    WorkoutTable.DATE,
                    WorkoutTable.DAYSELECTED
            };


            String selection = WorkoutTable.DATE  + "=1";

            Cursor tempCur = WorkoutTableReadable.query(
                    WorkoutTable.TABLE_NAME,
                    myProjection,
                    selection,
                    null,
                    null,
                    null,
                    null);

            //Log.d("INFO PRINT", "firstRunOfDay: " + Boolean.toString(firstRunOfDay));

            if(tempCur.moveToFirst()){
                ExerciseList temp = ExerciseList.newInstance(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME)) );
                WorkoutNames.add(temp);

                if(firstRunOfDay && !(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED)) == null)){
                    //if found, sets the firstRun to false
                    checkForDaySelected(tempCur, WorkoutNames.indexOf(temp));
                    //printDatabase(WorkoutTableReadable);

                }

            }

            while(tempCur.moveToNext()) {
                ExerciseList temp = ExerciseList.newInstance(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME)));
                WorkoutNames.add(temp);

                if(firstRunOfDay && !(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED)) == null)){
                    //if found, sets the firstRun to false
                    checkForDaySelected(tempCur,WorkoutNames.indexOf(temp));
                }

            }

            //if we populate the whole thing w/o finding a match
            firstRunOfDay = false;

        }

        public void checkForDaySelected(Cursor tempCur, int index){

                Calendar todayCal = Calendar.getInstance();
                int dayOfWeekIndex = todayCal.get(Calendar.DAY_OF_WEEK) - 1;
                String dayOfWeek;

                switch (dayOfWeekIndex) {
                    case 0:
                        dayOfWeek = "Sunday";
                        break;
                    case 1:
                        dayOfWeek = "Monday";
                        break;
                    case 2:
                        dayOfWeek = "Tuesday";
                        break;
                    case 3:
                        dayOfWeek = "Wednesday";
                        break;
                    case 4:
                        dayOfWeek = "Thursday";
                        break;
                    case 5:
                        dayOfWeek = "Friday";
                        break;
                    case 6:
                        dayOfWeek = "Saturday";
                        break;
                    default:
                        dayOfWeek = "None";
                        break;

                }

                //Log.d("INFO PRINT", "Day of the week (according to calender): " + dayOfWeek);
                //Log.d("INFO PRINT", "Database item being checked DAYSELECTED: " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED)));

                //Log.d("SUPER FLAGGGGGGGG", "BREAKING ON THIS ONE: " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED)));
                if(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED)).equals(dayOfWeek)){

                    //if found
                    firstRunOfDay = false;

                    lastWorkoutSelected = index;
                    FragmentManager fragmentManagerWorkoutListView = getActivity().getSupportFragmentManager();
                    fragmentManagerWorkoutListView.beginTransaction()
                            .replace(R.id.container, WorkoutNames.get(index), "exercise_fragment_tag")
                            .addToBackStack("Exercise Lists Fragment")
                            .commit();


                }

        }

        public void updateDatabaseRemoveDay(String workoutName){

            //SQLiteDatabase WorkoutTableDb  = new WorkoutTableDbHelper(getActivity().getApplicationContext()).getWritableDatabase();

            String[] myProjection = {
                    WorkoutTable.WORKOUT_NAME,
                    WorkoutTable.DATE,
                    WorkoutTable.DAYSELECTED
            };

            String selection = WorkoutTable.WORKOUT_NAME + "=? AND " + WorkoutTable.DATE  + " =?";
            String[] SelectionArgs = {workoutName,"1"};

            Cursor tempCur = WorkoutTableDb.query(
                    WorkoutTable.TABLE_NAME,
                    myProjection,
                    selection,
                    SelectionArgs,
                    null,
                    null,
                    null);


            if(tempCur.moveToFirst()){

                if(!tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED)).equals("")){

                    ContentValues values = new ContentValues();
                    values.put(WorkoutTable.WORKOUT_NAME, workoutName.toString());
                    values.put(WorkoutTable.DATE, "1");
                    values.put(WorkoutTable.DAYSELECTED, "");

                    WorkoutTableDb.update(
                            WorkoutTable.TABLE_NAME,
                            values,
                            selection,
                            SelectionArgs
                    );

                }

            }

            while(tempCur.moveToNext()){

                if(!tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED)).equals("")){

                    ContentValues values = new ContentValues();
                    values.put(WorkoutTable.WORKOUT_NAME, workoutName.toString());
                    values.put(WorkoutTable.DATE, "1");
                    values.put(WorkoutTable.DAYSELECTED, "");

                    WorkoutTableDb.update(
                            WorkoutTable.TABLE_NAME,
                            values,
                            selection,
                            SelectionArgs
                    );

                    //Log.d("updateDataBaseRemoveDay", "success, removed " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED)) + " from " + workoutName);
                }

            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_workout_list, container, false);

            //mNavigationDrawerFragment.restoreMenuButtons();

            workoutObjectArrayAdapter =
                    new WorkoutObjectArrayAdapter(getActivity() ,android.R.layout.simple_list_item_1, WorkoutNames);

            lvFragmentWorkoutList = (ListView) rootView.findViewById(R.id.lv_FragmentWorkoutList);
            lvFragmentWorkoutList.setAdapter(workoutObjectArrayAdapter);
            lvFragmentWorkoutList.setDivider(new ColorDrawable(0x00000000));
            lvFragmentWorkoutList.setDividerHeight(2);

            //Read from database, and fill in the WorkoutNames, the Listview will be filled by adapter

            if(WorkoutNames.isEmpty()) {
                populateWorkoutNames();
            }

            lvFragmentWorkoutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //Code here, for when I select an object in the WorkoutList
                    lastPosition = position; //for use in AddExercise

                    //set up handle on the workout name variable
                    String workoutNameHandle;
                    workoutNameHandle = WorkoutNames.get(position).return_workout_name();

                    if (NavigationDrawerFragment.mUndoWorkoutListEnabled) {

                        //Let people know that the button is enabled!
                        //used a toast, in the NavDrawerFrag

                        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                        adb.setTitle("Delete?");
                        adb.setMessage("Are you sure you want to delete " + workoutNameHandle);
                        final int positionToRemove = position;
                        adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                deleteFromDatabase(WorkoutNames.get(positionToRemove).return_workout_name());
                                //delete all data associated with this workout

                                WorkoutNames.remove(positionToRemove);
                                workoutObjectArrayAdapter.notifyDataSetChanged();

                                //refresh view
                                // Well after we make this change, we are returning it, so I dont think we'll have to invalidate it


                            }

                        });

                        adb.setNeutralButton("Remove Day", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //update database to remove the day
                                updateDatabaseRemoveDay(WorkoutNames.get(lastPosition).return_workout_name());

                            }
                        });

                        adb.setNegativeButton("Cancel", null);
                        adb.show();

                    } else {

                        lastWorkoutSelected = position;

                        //Trying to add ExerciseList to the WorkoutNames array instead of ExerciseObjects
                        //retrieve fragment before in the code below I replace the view.
                        
                        FragmentManager fragmentManagerWorkoutListView = getActivity().getSupportFragmentManager();
                        fragmentManagerWorkoutListView.beginTransaction()
                                .replace(R.id.container, WorkoutNames.get(position), "exercise_fragment_tag")
                                .addToBackStack("Exercise Lists Fragment")
                                .commit();

                    }



                }


            });

            return rootView;
        }


    }

}