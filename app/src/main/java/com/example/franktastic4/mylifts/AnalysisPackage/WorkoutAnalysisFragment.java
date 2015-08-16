package com.example.franktastic4.mylifts.AnalysisPackage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableDbHelper;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableReaderContract.WorkoutTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Franktastic4 on 7/26/15.
 */
public class WorkoutAnalysisFragment extends android.support.v4.app.Fragment {
    ExpandableListView myExpandableListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ArrayList<ExpandableListHeaderObject> mArrayOfHeadersObjects;
    com.example.franktastic4.mylifts.AnalysisPackage.ExpandableListAdapter listAdapter;

    WorkoutTableDbHelper mDbHelper;
    SQLiteDatabase WorkoutTableReadable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Database stuff
        mDbHelper = new WorkoutTableDbHelper(getActivity().getApplicationContext());
        WorkoutTableReadable = mDbHelper.getReadableDatabase();

    }

    public WorkoutAnalysisFragment() {}

    @Override
    public void onResume() {
        ((AnalysisActivity) getActivity()).setActionBarTitle("Analysis");
        //Have to set title bc i return to this page
        super.onResume();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_analysis, container, false);

        myExpandableListView = (ExpandableListView) rootView.findViewById(R.id.WorkoutsExpandableListView);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        fillDataHeader();
        fillChildData();

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        myExpandableListView.setAdapter(listAdapter);

        myExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {

                /* You must make use of the View v, find the view by id and extract the text as below*/

                TextView header = (TextView) parent.findViewById(R.id.lblListHeader);
                TextView child = (TextView) view.findViewById(R.id.lblListItem);

                VisualAnalysisFragment myVisualAnalysisFragment = new VisualAnalysisFragment();
                myVisualAnalysisFragment.setParameters(
                        header.getText().toString(),    // Workout
                        child.getText().toString(),     // Exercise
                        true                            // isWorkoutBoolean (true)
                        );

                FragmentManager fragmentManagerWorkoutListView = getActivity().getSupportFragmentManager();
                fragmentManagerWorkoutListView.beginTransaction()
                        .replace(R.id.container_analysis, myVisualAnalysisFragment, "visual_analysis_fragment_tag")
                        .addToBackStack("Visual Analysis Frag")
                        .commit();


                return true;
            }
        });

        return rootView;
    }

    public void fillDataHeader(){

        String[] myProjection = {
                WorkoutTable.WORKOUT_NAME,
                WorkoutTable.DATE
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

        mArrayOfHeadersObjects = new ArrayList<ExpandableListHeaderObject>();

        if(tempCur.moveToFirst()){

            ExpandableListHeaderObject myObj = new ExpandableListHeaderObject();
            myObj.setName(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME)));
            mArrayOfHeadersObjects.add(myObj);

        }

        while(tempCur.moveToNext()) {
            ExpandableListHeaderObject myObj = new ExpandableListHeaderObject();
            myObj.setName(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME)));
            mArrayOfHeadersObjects.add(myObj);
        }

    }

    public void fillChildData(){



        String[] myProjection = {
                WorkoutTable.WORKOUT_NAME,
                WorkoutTable.EXERCISE_NAME,
                WorkoutTable.DATE
        };


        String selection = WorkoutTable.DATE  + "=2";

        Cursor tempCur = WorkoutTableReadable.query(
                WorkoutTable.TABLE_NAME,
                myProjection,
                selection,
                null,
                null,
                null,
                null);

        //Log.d("INFO PRINT", "firstRunOfDay: " + Boolean.toString(firstRunOfDay));

        int counter = 0;
        while (counter < mArrayOfHeadersObjects.size()) {
            String tempName = mArrayOfHeadersObjects.get(counter).returnName();
            listDataHeader.add(tempName);
            List<String> temp = new ArrayList<String>();

            if(tempCur.moveToFirst()){

                //if the EXERCISE is a part of this workout, add it to temp
                if (tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME)).equals(tempName)) {
                    temp.add(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.EXERCISE_NAME)));
                }
            }



            while (tempCur.moveToNext()) {

                //if the EXERCISE is a part of this workout, add it to temp
                if (tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME)).equals(tempName)) {
                    temp.add(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.EXERCISE_NAME)));
                }


            }
            listDataChild.put(listDataHeader.get(counter), temp);
            counter++;
        }


    }


}
