package com.example.franktastic4.mylifts.WorkoutListPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.franktastic4.mylifts.NavigationDrawerFragment;
import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableReaderContract.WorkoutTable;

import java.util.ArrayList;

public class ExerciseList extends android.support.v4.app.Fragment{
    public   ExerciseObjectArrayAdapter mAdapter;
    private String workoutName;
    public   ListView lvFragmentExerciseList;
    public   ArrayList<ExerciseInstance> ExerciseNames = new ArrayList<ExerciseInstance>();

    //exerciseName is actually the workoutName
    public String exerciseName;
    public int lastPositionExerciseList;
    ActionBar actionBar;

    WorkoutTableDbHelper mDbHelper;
    static SQLiteDatabase WorkoutTableDb;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    public static ExerciseList newInstance(String exerciseName) {
        ExerciseList fragment = new ExerciseList();
        Bundle args = new Bundle();
        args.putString("exerciseName" ,exerciseName);
        fragment.setArguments(args);
        return fragment;
    }
    public ExerciseList() {}

    public String return_workout_name(){

        //is actually the workout name
        return getArguments().getString("exerciseName");
    }

    public void printDatabase(){

        SQLiteDatabase WorkoutTableReadable  = new WorkoutTableDbHelper(getActivity().getApplicationContext()).getReadableDatabase();

        String[] myProjection = {
                WorkoutTable.WORKOUT_NAME,
                WorkoutTable.EXERCISE_NAME,
                WorkoutTable.DATE
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
        while(tempCur.moveToNext()){

            Log.d("TABLE ITEM", "Looking for " + workoutName + " " +
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME))
                    + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.EXERCISE_NAME))
                    + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE))
            );

        }


    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title


        ((WorkoutList) getActivity()).setActionBarTitle(workoutName + " Exercises");
        setUndoBooleanFalse();

    }

    public void setUndoBooleanFalse(){

        NavigationDrawerFragment.mUndoWorkoutListEnabled = false;
        NavigationDrawerFragment.mUndoExerciseListEnabled = false;
        NavigationDrawerFragment.mUndoExerciseInstanceEnabled = false;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUndoBooleanFalse();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getActivity().getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        if (getArguments() != null) {
            workoutName = getArguments().getString("exerciseName");
        }

        mDbHelper = new WorkoutTableDbHelper(getActivity().getApplicationContext());
        WorkoutTableDb = mDbHelper.getWritableDatabase();


    }

    public void deleteFromDatabase(String deleteExerciseName){

        //Don't I have to check the date = 2? NVM
        //if we delete everything related to Squats

        String whereClause = WorkoutTableReaderContract.WorkoutTable.EXERCISE_NAME + "=?";
        String[] whereArgs = new String[] {deleteExerciseName };
        WorkoutTableDb.delete(WorkoutTableReaderContract.WorkoutTable.TABLE_NAME, whereClause, whereArgs);

    }

    public void populateExerciseNames(){

        SQLiteDatabase WorkoutTableReadable  = new WorkoutTableDbHelper(getActivity().getApplicationContext()).getReadableDatabase();

        String[] myProjection = {
                WorkoutTable.WORKOUT_NAME,
                WorkoutTable.EXERCISE_NAME,
                WorkoutTable.DATE
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection = WorkoutTable.WORKOUT_NAME + "=? AND " + WorkoutTable.DATE  + " =?";

        String[] SelectionArgs = {workoutName,"2"};

        Cursor tempCur = WorkoutTableReadable.query(
                WorkoutTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);



        if(tempCur.moveToFirst()){

            ExerciseInstance temp = ExerciseInstance.newInstance(tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.EXERCISE_NAME)), workoutName );
            ExerciseNames.add(temp);

        }

        while(tempCur.moveToNext()){

            ExerciseInstance temp = ExerciseInstance.newInstance(tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.EXERCISE_NAME)), workoutName );
            ExerciseNames.add(temp);

        }

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_exercise_list, container, false);

        mAdapter = new ExerciseObjectArrayAdapter(getActivity() ,android.R.layout.simple_list_item_1, ExerciseNames);
        lvFragmentExerciseList = (ListView) rootView.findViewById(R.id.lv_FragmentExerciseList);
        lvFragmentExerciseList.setAdapter(mAdapter);
        lvFragmentExerciseList.setDivider(new ColorDrawable(0x00000000));
        lvFragmentExerciseList.setDividerHeight(2);

        if(ExerciseNames.isEmpty()) {
            populateExerciseNames();
        }

        lvFragmentExerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                exerciseName = ExerciseNames.get(position).return_exercise_name();

                //if the - sign was pressed, you are on remove mode.

                if (NavigationDrawerFragment.mUndoExerciseListEnabled) {

                    //Let people know that the button is enabled!
                    //used a toast, in the NavDrawerFrag

                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                    adb.setTitle("Delete?");
                    adb.setMessage("Are you sure you want to delete " + exerciseName);
                    final int positionToRemove = position;
                    adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            deleteFromDatabase(exerciseName);
                            ExerciseNames.remove(positionToRemove);
                            mAdapter.notifyDataSetChanged();

                            //refresh view
                            // Well after we make this change, we are returning it, so I dont think we'll have to invalidate it


                        }

                    });

                    adb.setNegativeButton("Cancel", null);
                    adb.show();

                } else {

                    //This is so I can access this from ExerciseInstance, when I am adding some data
                    lastPositionExerciseList = position;

                    FragmentManager fragmentManagerWorkoutListView = getActivity().getSupportFragmentManager();
                    fragmentManagerWorkoutListView.beginTransaction()
                            .replace(R.id.container, ExerciseNames.get(position), "exercise_instance_tag")
                            .addToBackStack("Exercise Instance Fragment")
                            .commit();

                }

            }
        });



        return rootView;
    }


}

