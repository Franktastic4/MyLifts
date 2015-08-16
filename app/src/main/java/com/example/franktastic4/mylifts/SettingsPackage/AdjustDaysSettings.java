package com.example.franktastic4.mylifts.SettingsPackage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableDbHelper;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableReaderContract;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableReaderContract.WorkoutTable;

import java.util.ArrayList;

/**
 * Created by Franktastic4 on 7/20/15.
 */
public class AdjustDaysSettings extends android.support.v4.app.Fragment {

    ArrayList<String> listOfWorkouts;
    ArrayAdapter<String> myAdapter;
    private boolean addDayBoolean = true;

    WorkoutTableDbHelper mDbHelper;
    SQLiteDatabase WorkoutTableDb;

    public void setBoolean(boolean newBool){
        addDayBoolean = newBool;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new WorkoutTableDbHelper(getActivity().getApplicationContext());
        WorkoutTableDb = mDbHelper.getWritableDatabase();
    }

    public AdjustDaysSettings(){}

    public void onResume(){
        super.onResume();
        ((SettingsActivity) getActivity()).setActionBarTitle("Adjust Days");
    }

    public void populateListView(){

        SQLiteDatabase WorkoutTableReadable  = new WorkoutTableDbHelper(getActivity().getApplicationContext()).getReadableDatabase();

        String[] myProjection = {
                WorkoutTable.WORKOUT_NAME,
                WorkoutTable.DATE,
                WorkoutTable.DAYSELECTED
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection = WorkoutTable.DATE  + " =? AND " + WorkoutTable.DAYSELECTED + " !=? AND " + WorkoutTable.DAYSELECTED + " !=?";

        String[] SelectionArgs;
        if(addDayBoolean){
            //if we're adding we want all
            SelectionArgs = new String[]{"1","placeholder","otherplaceholder"};
        }else {
            //if we are removing we only want days that are assigned
            SelectionArgs = new String[]{"1", "", "null"};
        }

        Cursor tempCur = WorkoutTableReadable.query(
                WorkoutTableReaderContract.WorkoutTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);



        if(tempCur.moveToFirst()){

           listOfWorkouts.add(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME)));

        }

        while(tempCur.moveToNext()){

            listOfWorkouts.add(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME)));

        }



    }

    public void updateDatabaseWithDay(String daySelectedString, int position){

        String[] myProjection = {
                WorkoutTable.WORKOUT_NAME,
                WorkoutTable.DATE,
                WorkoutTable.DAYSELECTED
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection = WorkoutTable.WORKOUT_NAME + "=? AND " + WorkoutTable.DATE  + " =?";

        String[] SelectionArgs = {listOfWorkouts.get(position), "1"};

        Cursor tempCur = WorkoutTableDb.query(
                WorkoutTableReaderContract.WorkoutTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

        if(tempCur.moveToFirst()){

            //if(!tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED)).equals("")){

                ContentValues values = new ContentValues();
                values.put(WorkoutTable.WORKOUT_NAME, listOfWorkouts.get(position));
                values.put(WorkoutTable.DATE, "1");
                values.put(WorkoutTable.DAYSELECTED, daySelectedString);

                WorkoutTableDb.update(
                        WorkoutTable.TABLE_NAME,
                        values,
                        selection,
                        SelectionArgs
                );

            //}

        }

        while(tempCur.moveToNext()){

            //if(!tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED)).equals("")){

                ContentValues values = new ContentValues();
                values.put(WorkoutTable.WORKOUT_NAME, listOfWorkouts.get(position));
                values.put(WorkoutTable.DATE, "1");
                values.put(WorkoutTable.DAYSELECTED, daySelectedString);

                WorkoutTableDb.update(
                        WorkoutTable.TABLE_NAME,
                        values,
                        selection,
                        SelectionArgs
                );

            //}

        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_adjust_days_settings, container, false);

        listOfWorkouts = new ArrayList<String>();
        final ListView listView = (ListView) rootView.findViewById(R.id.adjustDaysSettingsListView);
        myAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,listOfWorkouts);
        listView.setAdapter(myAdapter);
        populateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //On item clicked
                if (addDayBoolean) {

                    //adb to present spinner wheel

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Tap To Select");

                    //Didn't need a custom spinner layout. I needed a custom textview layout.
                    //in the SpinnerAdapter I used android.R.layout.simple_spinner_item.
                    //Now I made a custom layout where its just a textview where I can specify the textsize

                    View myView = getLayoutInflater(null).inflate(R.layout.spinner_layout_file, null);
                    final Spinner mySpinner = (Spinner) myView.findViewById(R.id.spinner_layout_id_1);
                    String[] items = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                    ArrayAdapter<String> mySpinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout_text_view, items);
                    mySpinner.setAdapter(mySpinnerAdapter);
                    builder.setView(myView);

                    builder.setPositiveButton("Yes", new AlertDialog.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            updateDatabaseWithDay(mySpinner.getSelectedItem().toString(), position);
                            Log.d("FLAG", "Day Picked: " + mySpinner.getSelectedItem().toString());
                            dialog.dismiss();

                        }

                    });
                    builder.setNegativeButton("Cancel", null);

                    final Dialog dialog = builder.create();
                    dialog.show();

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                    Toast.makeText(getActivity(), "Success, assigned " + listOfWorkouts.get(position).toString() + "to " + "Date", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getActivity(), "Success, removed day from " + listOfWorkouts.get(position).toString(), Toast.LENGTH_SHORT).show();
                    updateDatabaseWithDay(null,position);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();

                }

            }
        });




        return rootView;
    }


}
