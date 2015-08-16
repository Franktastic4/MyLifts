package com.example.franktastic4.mylifts.AnalysisPackage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableDbHelper;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableReaderContract.MeasurementTable;
import com.example.franktastic4.mylifts.R;

import java.util.ArrayList;

/**
 * Created by Franktastic4 on 7/26/15.
 */
public class MeasurementAnalysisFragment extends android.support.v4.app.Fragment {

    MeasurementTableDbHelper mDbHelper;
    SQLiteDatabase MeasurementTableReadable;
    ListAdapter myListAdapter;
    ArrayList<String> listOfStrings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Database stuff
        mDbHelper = new MeasurementTableDbHelper(getActivity().getApplicationContext());
        MeasurementTableReadable = mDbHelper.getReadableDatabase();

    }

    public MeasurementAnalysisFragment() {
    }

    @Override
    public void onResume() {
        ((AnalysisActivity) getActivity()).setActionBarTitle("Analysis");
        //Have to set title bc i return to this page
        super.onResume();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_analysis_measurement, container, false);
        listOfStrings = new ArrayList<String>();
        fillListView();

        myListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                listOfStrings);

        ListView myListView = (ListView) rootView.findViewById(R.id.MeasurementAnalysisListView);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callVisualAnalysisFragment(position);
            }
        });

        myListView.setAdapter(myListAdapter);

        return rootView;
    }

    public void fillListView() {

        String[] myProjection = {
                MeasurementTable.MEASUREMENT,
                MeasurementTable.CALENDAR
        };


        String selection = MeasurementTable.CALENDAR + "=1";

        Cursor tempCur = MeasurementTableReadable.query(
                MeasurementTable.TABLE_NAME,
                myProjection,
                selection,
                null,
                null,
                null,
                null);

        if (tempCur.moveToFirst()) {
            listOfStrings.add(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT)));
        }

        while(tempCur.moveToNext()){
            listOfStrings.add(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT)));
        }

    }

    public void  callVisualAnalysisFragment(int position){

        VisualAnalysisFragment myVisualAnalysisFragment = new VisualAnalysisFragment();
        myVisualAnalysisFragment.setParameters(
                listOfStrings.get(position),    // Workout
                listOfStrings.get(position),     // Exercise
                false                            // isWorkoutBoolean (true)
        );

        FragmentManager fragmentManagerWorkoutListView = getActivity().getSupportFragmentManager();
        fragmentManagerWorkoutListView.beginTransaction()
                .replace(R.id.container_analysis, myVisualAnalysisFragment, "visual_analysis_fragment_tag")
                .addToBackStack("Visual Analysis Frag")
                .commit();

    }



}
