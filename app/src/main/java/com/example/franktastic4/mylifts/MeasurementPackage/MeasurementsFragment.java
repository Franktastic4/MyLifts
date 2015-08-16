package com.example.franktastic4.mylifts.MeasurementPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableReaderContract.MeasurementTable;
import com.example.franktastic4.mylifts.R;

import java.util.ArrayList;



/**
 * Created by Franktastic4 on 6/25/15.
 */
public class MeasurementsFragment extends android.support.v4.app.Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public ListView myMeasurementFragmentListView;
    public ArrayList<MeasurementInstance> measurementInstanceArray = new ArrayList<MeasurementInstance>();
    public MeasurementInstanceArrayAdapter myMeasureInstanceArrayAdapter;
    public FragmentManager myFragmentManager;
    public int lastPosition;

    MeasurementTableDbHelper mDbHelper;
    SQLiteDatabase MeasurementTableDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new MeasurementTableDbHelper(getActivity().getApplicationContext());
        MeasurementTableDB = mDbHelper.getWritableDatabase();
    }

    public MeasurementsFragment() {}

    public static MeasurementsFragment newInstance(int sectionNumber) {
        MeasurementsFragment fragment = new MeasurementsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        ((MeasurementsActivity) getActivity()).setActionBarTitle("Measurements");
        //Have to set title bc i return to this page
        super.onResume();
    }

    public void printDatabase() {

        SQLiteDatabase MeasurementTableReadable = new MeasurementTableDbHelper(getActivity().getApplicationContext()).getReadableDatabase();

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

    private void populateMeasurementFragment(){

        SQLiteDatabase MeasurementTableReadable  = new MeasurementTableDbHelper(getActivity().getApplicationContext()).getReadableDatabase();

        String[] myProjection = {
                MeasurementTable.MEASUREMENT,
                MeasurementTable.CALENDAR,
        };


        String selection = MeasurementTable.CALENDAR  + "=1";

        Cursor tempCur = MeasurementTableReadable.query(
                MeasurementTable.TABLE_NAME,
                myProjection,
                selection,
                null,
                null,
                null,
                null);

        if(tempCur.moveToFirst()){
            MeasurementInstance temp = MeasurementInstance.newInstance(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT)) );
            temp.setMeasurementInstanceName(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT)));
            measurementInstanceArray.add(temp);
        }

        while(tempCur.moveToNext()){
            MeasurementInstance temp = MeasurementInstance.newInstance(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT)) );
            temp.setMeasurementInstanceName(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT)));
            measurementInstanceArray.add(temp);
        }

    }

    private void removeFromDatabase(String deleteString){

        String whereClause = MeasurementTable.MEASUREMENT + "=?";
        String[] whereArgs = new String[] {deleteString };
        MeasurementTableDB.delete(MeasurementTable.TABLE_NAME, whereClause, whereArgs);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_measurements, container, false);

        myMeasureInstanceArrayAdapter =
                new MeasurementInstanceArrayAdapter(getActivity() ,android.R.layout.simple_list_item_1, measurementInstanceArray);
        myMeasurementFragmentListView = (ListView) rootView.findViewById(R.id.fragment_measurement_listView);
        myMeasurementFragmentListView.setAdapter(myMeasureInstanceArrayAdapter);
        myMeasurementFragmentListView.setDivider(new ColorDrawable(0x00000000));
        myMeasurementFragmentListView.setDividerHeight(2);

        if(measurementInstanceArray.isEmpty()) {
            populateMeasurementFragment();
            printDatabase();
        }

        myMeasurementFragmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //if remove is enabled
                if (MeasurementsActivity.mNavigationDrawerFragment.mUndoMeasurementsFragment) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                    adb.setTitle("Delete?");
                    adb.setMessage("Are you sure you want to delete " + measurementInstanceArray.get(position).return_measurement_instance_name());
                    final int positionToRemove = position;
                    adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            removeFromDatabase(measurementInstanceArray.get(positionToRemove).return_measurement_instance_name());
                            measurementInstanceArray.remove(positionToRemove);
                            myMeasureInstanceArrayAdapter.notifyDataSetChanged();
                        }

                    });

                    adb.setNegativeButton("Cancel", null);
                    adb.show();

                } else {

                    lastPosition = position;
                    //fragment manager
                    myFragmentManager = getActivity().getSupportFragmentManager();
                    myFragmentManager.beginTransaction()
                            .replace(R.id.container_measurements, measurementInstanceArray.get(position), "measurements_instance_tag")
                            .addToBackStack("measurement_fragment_tag")
                            .commit();
                }

            }

        });


        return rootView;
    }

}
