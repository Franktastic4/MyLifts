package com.example.franktastic4.mylifts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.franktastic4.mylifts.JournalPackage.AddJournalEntry;
import com.example.franktastic4.mylifts.JournalPackage.JournalFragment;
import com.example.franktastic4.mylifts.JournalPackage.JournalTableDbHelper;
import com.example.franktastic4.mylifts.JournalPackage.JournalTableReaderContract;
import com.example.franktastic4.mylifts.MeasurementPackage.AddGoal;
import com.example.franktastic4.mylifts.MeasurementPackage.AddMeasurement;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementInstance;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementsActivity;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementsFragment;
import com.example.franktastic4.mylifts.WorkoutListPackage.AddExercise;
import com.example.franktastic4.mylifts.WorkoutListPackage.AddWorkout;
import com.example.franktastic4.mylifts.WorkoutListPackage.ExerciseInstance;
import com.example.franktastic4.mylifts.WorkoutListPackage.ExerciseList;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutList;

import java.util.Calendar;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private NavigationArrayAdapter mNavigationArrayAdapter;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private ExerciseList myExerciseListFragment;
    private ExerciseInstance myExerciseInstanceFragment;
    private MeasurementsFragment myMeasurementsFragment;
    private MeasurementInstance myMeasurementsInstance;
    private JournalFragment myJournalFragment;

    private AddExercise addExerciseFragment;
    private AddWorkout addWorkoutFragment;
    private AddMeasurement addMeasurementFragment;

    private WorkoutList.Workout_List_Fragment myWorkoutListFragment;

    private String savedTitle;

    //Can only remove from fragment that I'm looking at
    public static boolean mUndoWorkoutListEnabled = false;
    public static boolean mUndoExerciseListEnabled = false;
    public static boolean mUndoExerciseInstanceEnabled = false;
    public static boolean mUndoMeasurementsFragment = false;

    public static ActionMenuItemView plusButton;
    public static ActionMenuItemView minusButton;
    public static ActionMenuItemView undoTappedButton;


    public NavigationDrawerFragment() {}

    public void updateSelectedItem(int newPos){
        mDrawerListView.setItemChecked(newPos, true);
    }

    public DrawerLayout returnDrawerLayout(){

        return mDrawerLayout;
    }

    public void setUndoBooleanFalse(){

        mUndoWorkoutListEnabled = false;
        mUndoExerciseListEnabled = false;
        mUndoExerciseInstanceEnabled = false;
        mUndoMeasurementsFragment = false;

    }

    @Override
    public void onResume(){
        super.onResume();
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("MyLifts");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        mNavigationArrayAdapter = new NavigationArrayAdapter(getActivity(),
                new String[]{
                getString(R.string.Navigation_Drawer_Home),
                getString(R.string.Navigation_Drawer_Measurements),
                getString(R.string.Navigation_Drawer_Journal),
                getString(R.string.Navigation_Drawer_Calendar),
                getString(R.string.Navigation_Drawer_Analysis),
                getString(R.string.Navigation_Drawer_Settings),
                },
                new Integer[]{
                        R.drawable.barbel_icon_bad,
                        R.drawable.tape_icon_bad,
                        R.drawable.journal_icon_bad,
                        R.drawable.cal_icon_bad,
                        R.drawable.graph_icon_bad,
                        R.drawable.cog_icon_bad
                }
        );

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mDrawerListView.setAdapter(mNavigationArrayAdapter);

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);


                if(mCallbacks != null) {
                    mCallbacks.onDrawerClosedCallback();
                }

                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if(mCallbacks != null) {
                    mCallbacks.onDrawerOpenedCallback();
                }


                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;

        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }


    }

    public void setMenuButtons(){

        myExerciseListFragment = (ExerciseList)getFragmentManager().findFragmentByTag("exercise_fragment_tag");
        myExerciseInstanceFragment = (ExerciseInstance)getFragmentManager().findFragmentByTag("exercise_instance_tag");
        myWorkoutListFragment = (WorkoutList.Workout_List_Fragment)getFragmentManager().findFragmentByTag("workoutList_fragment_tag");

        myMeasurementsFragment = (MeasurementsFragment)getFragmentManager().findFragmentByTag("measurements_fragment_tag");
        myMeasurementsInstance = (MeasurementInstance)getFragmentManager().findFragmentByTag("measurements_instance_tag");

        addExerciseFragment = (AddExercise)getFragmentManager().findFragmentByTag("add_exercise_tag");
        addWorkoutFragment = (AddWorkout)getFragmentManager().findFragmentByTag("add_workout_tag");
        addMeasurementFragment = (AddMeasurement)getFragmentManager().findFragmentByTag("add_measurement_tag");

        if(myWorkoutListFragment !=null && myWorkoutListFragment.isVisible()){
            restoreButtons();
        }else if(myExerciseListFragment != null &&myExerciseListFragment.isVisible()){
            restoreButtons();
        }else if(myExerciseInstanceFragment !=null && myExerciseInstanceFragment.isVisible()) {
            hideButtons();
        }else if(myMeasurementsFragment != null && myMeasurementsFragment.isVisible()){
            restoreButtons();
        }else if(myMeasurementsInstance != null && myMeasurementsInstance.isVisible()){
            restoreButtons();
        }else if(addExerciseFragment !=null && addExerciseFragment.isVisible()){
            hideButtons();
        }else if(addWorkoutFragment !=null && addWorkoutFragment.isVisible()){
            hideButtons();
        }else if(addMeasurementFragment != null && addMeasurementFragment.isVisible()){
            hideButtons();
        }

    }

    public void hideButtons(){

        plusButton = (ActionMenuItemView)getActivity().findViewById(R.id.action_example);
        minusButton = (ActionMenuItemView)getActivity().findViewById(R.id.undo);
        undoTappedButton = (ActionMenuItemView)getActivity().findViewById(R.id.undo_tapped);

        plusButton.setVisibility(View.GONE);
        minusButton.setVisibility(View.GONE);
        undoTappedButton.setVisibility(View.GONE);

    }

    public void restoreButtons(){

        plusButton = (ActionMenuItemView)getActivity().findViewById(R.id.action_example);
        minusButton = (ActionMenuItemView)getActivity().findViewById(R.id.undo);
        undoTappedButton = (ActionMenuItemView)getActivity().findViewById(R.id.undo_tapped);

        plusButton.setVisibility(View.VISIBLE);
        minusButton.setVisibility(View.VISIBLE);
        undoTappedButton.setVisibility(View.GONE);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        plusButton = (ActionMenuItemView)getActivity().findViewById(R.id.action_example);
        minusButton = (ActionMenuItemView)getActivity().findViewById(R.id.undo);
        undoTappedButton = (ActionMenuItemView)getActivity().findViewById(R.id.undo_tapped);

        myExerciseListFragment = (ExerciseList)getFragmentManager().findFragmentByTag("exercise_fragment_tag");
        myExerciseInstanceFragment = (ExerciseInstance)getFragmentManager().findFragmentByTag("exercise_instance_tag");
        myWorkoutListFragment = (WorkoutList.Workout_List_Fragment)getFragmentManager().findFragmentByTag("workoutList_fragment_tag");
        myMeasurementsFragment = (MeasurementsFragment)getFragmentManager().findFragmentByTag("measurements_fragment_tag");
        myMeasurementsInstance = (MeasurementInstance)getFragmentManager().findFragmentByTag("measurements_instance_tag");
        myJournalFragment = (JournalFragment)getFragmentManager().findFragmentByTag("journal_fragment_tag");


        if (item.getItemId() == R.id.action_example) {

            setUndoBooleanFalse();

            if (myExerciseListFragment != null && myExerciseListFragment.isVisible()) {

                //if we're on the ExerciseList

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new AddExercise().newInstance(), "add_exercise_tag")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null) // enables back key
                        .commit();

            }else if(myWorkoutListFragment != null && myWorkoutListFragment.isVisible()){

                //if we're on the myWorkoutListFragment

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new AddWorkout().newInstance(null, null), "add_workout_tag")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null) // enables back key
                        .commit();

            }else if(myMeasurementsFragment != null && myMeasurementsFragment.isVisible()){

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_measurements, new AddMeasurement().newInstance(), "add_measurement_tag")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null) // enables back key
                        .commit();

            }else if(myExerciseInstanceFragment != null && myExerciseInstanceFragment.isVisible()){

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

                final EditText edittext= new EditText(getActivity());
                alert.setMessage("Add a note,it will be saved to journal");
                alert.setTitle("Add Note");
                alert.setView(edittext);

                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        //String YouEditTextValue = edittext.getText();
                        addToJournalMethod(edittext.getText().toString());

                    }
                });

                alert.setNegativeButton("Cancel", null);
                alert.show();

            }else if(myMeasurementsInstance != null && myMeasurementsInstance.isVisible()){

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_measurements, new AddGoal().newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null) // enables back key
                        .commit();

            }else if(myJournalFragment != null && myJournalFragment.isVisible()){

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_journal, new AddJournalEntry().newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null) // enables back key
                        .commit();

            }

                //maybe in new pages, don't let it do anything yet



            return true;

        }else if (item.getItemId() == R.id.undo) {

            //if we hit this button while cancel is enabled, we should turn it off.

            if(myExerciseListFragment != null && myExerciseListFragment.isVisible()) {

                //if(mUndoExerciseListEnabled){
                //    setUndoBooleanFalse();
                //    Toast.makeText(getActivity().getBaseContext(), "Remove Disabled", Toast.LENGTH_SHORT).show();
                //}

                mUndoWorkoutListEnabled = false;
                mUndoExerciseListEnabled = true;
                mUndoExerciseInstanceEnabled = false;


                if(WorkoutList.mActivityIsFront) {
                    minusButton.setVisibility(View.GONE);
                    undoTappedButton.setVisibility(View.VISIBLE);
                }


                Toast.makeText(getActivity().getBaseContext(), "Remove is enabled, tap a exercise to remove it", Toast.LENGTH_SHORT).show();


            }else if(myWorkoutListFragment != null && myWorkoutListFragment.isVisible()){

                if(mUndoWorkoutListEnabled){
                    setUndoBooleanFalse();
                    Toast.makeText(getActivity().getBaseContext(), "Remove Disabled", Toast.LENGTH_SHORT).show();
                }

                mUndoWorkoutListEnabled = true;
                mUndoExerciseListEnabled = false;
                mUndoExerciseInstanceEnabled = false;


                if(WorkoutList.mActivityIsFront) {
                    minusButton.setVisibility(View.GONE);
                    undoTappedButton.setVisibility(View.VISIBLE);
                }


                Toast.makeText(getActivity().getBaseContext(), "Remove is enabled, tap a workout to remove it", Toast.LENGTH_SHORT).show();

            }else if(myExerciseInstanceFragment != null && myExerciseInstanceFragment.isVisible()) {

                //if(mUndoExerciseInstanceEnabled){
                //    setUndoBooleanFalse();
                //    Toast.makeText(getActivity().getBaseContext(), "Remove Disabled", Toast.LENGTH_SHORT).show();
                //}

                mUndoWorkoutListEnabled = false;
                mUndoExerciseListEnabled = false;
                mUndoExerciseInstanceEnabled = true;


                if (WorkoutList.mActivityIsFront) {
                    minusButton.setVisibility(View.GONE);
                    undoTappedButton.setVisibility(View.VISIBLE);
                }


                Toast.makeText(getActivity().getBaseContext(), "UNDO", Toast.LENGTH_SHORT).show();

            }else if(myMeasurementsFragment != null && myMeasurementsFragment.isVisible()){

                mUndoMeasurementsFragment = true;

                if (MeasurementsActivity.mMeasurementActivityIsFront) {
                    minusButton.setVisibility(View.GONE);
                    undoTappedButton.setVisibility(View.VISIBLE);
                }


            }else{

                setUndoBooleanFalse();
            }

            return true;

        }else if(item.getItemId() == R.id.undo_tapped){

            //This will be the "Stop Delete" button
            setUndoBooleanFalse();
            Toast.makeText(getActivity().getBaseContext(), "UNDO", Toast.LENGTH_SHORT).show();

            //Make - button visible again
            minusButton.setVisibility(View.VISIBLE);
            undoTappedButton.setVisibility(View.GONE);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */

    public void addToJournalMethod(String newJournalEntry){

        JournalTableDbHelper mDbHelper = new JournalTableDbHelper(getActivity().getApplicationContext());
        SQLiteDatabase JournalTableDB = mDbHelper.getReadableDatabase();


        if(!newJournalEntry.equals("")) {

            ContentValues values = new ContentValues();
            values.put(JournalTableReaderContract.JournalTable.CALENDAR, Calendar.getInstance().getTime().toString());
            values.put(JournalTableReaderContract.JournalTable.ENTRY, newJournalEntry);
            JournalTableDB.insert(JournalTableReaderContract.JournalTable.TABLE_NAME, null, values);

        }

    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */

        //Make so first call doesn't do anything, did a if and ++

        void onNavigationDrawerItemSelected(int position);
        void onDrawerClosedCallback();
        void onDrawerOpenedCallback();


    }



}
