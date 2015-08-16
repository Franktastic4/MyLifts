package com.example.franktastic4.mylifts.SettingsPackage;

/**
 * Created by Franktastic4 on 7/16/15.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableDbHelper;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableReaderContract.MeasurementTable;
import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableDbHelper;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableReaderContract;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableReaderContract.WorkoutTable;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FeaturesFragment extends Fragment{

    ArrayAdapter<String> featuresListAdapter;
    ArrayList arrayOfImports;

    WorkoutTableDbHelper mDbHelper;
    SQLiteDatabase WorkoutTableDb;

    MeasurementTableDbHelper mDbHelperMeasurement;
    SQLiteDatabase MeasurementTableDb;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new WorkoutTableDbHelper(getActivity().getApplicationContext());
        WorkoutTableDb = mDbHelper.getWritableDatabase();

        mDbHelperMeasurement = new MeasurementTableDbHelper(getActivity().getApplicationContext());
        MeasurementTableDb = mDbHelperMeasurement.getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_features, container, false);

        featuresListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                new String[]{
                        "Import Workouts",
                        "Import Measurements",
                        "Add Day Associated to Workout",
                        "Remove Day Associated from Workout",
                        "Enable GoalView (Measurements)",
                        "Disable GoalView (Measurements)",
                        "Hide empty days (Journal)",
                        "Show empty days (Journal)",
                        "Themes"
                }
        );

        ListView exportListView = (ListView) rootView.findViewById(R.id.featuresListView);
        exportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                featuresListSelected(position);
            }
        });

        exportListView.setAdapter(featuresListAdapter);

        return rootView;
    }

    public void printDatabase() {

        SQLiteDatabase WorkoutTableReadable = new WorkoutTableDbHelper(getActivity().getApplicationContext()).getReadableDatabase();

        String[] myProjection = {
                WorkoutTableReaderContract.WorkoutTable.WORKOUT_NAME,
                WorkoutTableReaderContract.WorkoutTable.EXERCISE_NAME,
                WorkoutTableReaderContract.WorkoutTable.DATE,
                WorkoutTableReaderContract.WorkoutTable.SET,
                WorkoutTableReaderContract.WorkoutTable.REPS,
                WorkoutTableReaderContract.WorkoutTable.WEIGHT,
                WorkoutTableReaderContract.WorkoutTable.DAYSELECTED
        };

        Cursor tempCur = WorkoutTableReadable.query(
                WorkoutTableReaderContract.WorkoutTable.TABLE_NAME,
                myProjection,
                null,
                null,
                null,
                null,
                null);

        tempCur.moveToFirst();
        while (tempCur.moveToNext()) {

            Log.d("TABLE ITEM", tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.WORKOUT_NAME))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.EXERCISE_NAME))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.DATE))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.SET))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.REPS))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.WEIGHT))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.DAYSELECTED))
            );

        }
    }

    public void featuresListSelected(int index) {

        switch(index){
            case 0:
                importWorkouts(true);
                break;

            case 1:
                importWorkouts(false);
                break;

            case 2:
                addDayToWorkout();
                //printDatabase();
                break;

            case 3:
                removeDayFromWorkout();
                break;

            case 4:
                enableGoalView();
                break;

            case 5:
                disableGoalView();
                break;

            case 6:
                hideEmptyDays();
                break;

            case 7:
                showEmptyDays();
                break;

            case 8: //Themes
                break;

            default: break;

        }


    }

    public void hideEmptyDays(){

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.HideEmptyJournalEntry), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.HideEmptyJournalEntry), true);
        editor.apply();
        Toast.makeText(getActivity(), "Hiding empty days in journal", Toast.LENGTH_SHORT).show();

    }

    public void showEmptyDays(){

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.HideEmptyJournalEntry), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.HideEmptyJournalEntry), false);
        editor.apply();
        Toast.makeText(getActivity(), "Showing empty days in journal", Toast.LENGTH_SHORT).show();

    }

    public void disableGoalView(){

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.GoalViewKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.GoalViewKey), false);
        editor.apply();
        Toast.makeText(getActivity(), "GoalView is disabled", Toast.LENGTH_SHORT).show();

    }

    public void enableGoalView(){

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.GoalViewKey), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.GoalViewKey), true);
        editor.apply();
        Toast.makeText(getActivity(), "GoalView is enabled", Toast.LENGTH_SHORT).show();

    }

    public void importWorkouts(boolean isWorkout){

        //An array with all the paths to the CSV files.
        //Using on click listener find which ones and thne open them.
        //Backup current workouts, wipe and write the new ones in

        arrayOfImports = new ArrayList<String>();

        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        File f = new File(baseDir + File.separator + "MyLiftsData");
        importWorkoutsHelper(f);
        importWorkoutsHelperDOWNLOAD(new File(baseDir + File.separator + "Download"));

        final boolean isWorkoutVariable = isWorkout;

        if(!arrayOfImports.isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //builder.setTitle("Available Workouts");

            final ListView listViewImports = new ListView(getActivity());

            ArrayAdapter<String> listViewImportsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, arrayOfImports);
            listViewImports.setAdapter(listViewImportsAdapter);

            builder.setView(listViewImports);
            final Dialog dialog = builder.create();

            listViewImports.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if(isWorkoutVariable) {
                        importWorkoutsSelected(arrayOfImports.get(position).toString());
                        dialog.dismiss();
                    }else{
                        importMeasurementSelected(arrayOfImports.get(position).toString());
                        dialog.dismiss();
                    }

                }
            });

            dialog.show();


        }

    }

    public void importWorkoutsHelper(File dir) {

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    importWorkoutsHelper(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(".csv")) {

                        //Make a list of these and then present in an adb to user
                        String tmp = listFile[i].getAbsolutePath();
                        arrayOfImports.add(tmp);
                        //Log.d("FOUND", "found a csv file");

                    }
                }
            }
        }
    }

    public void importWorkoutsHelperDOWNLOAD(File dir){

        //String baseDir2 = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        //File f2 = new File(baseDir2 + File.separator + "Download");
        //File listFile2[] = f2.listFiles();
        File listFile2[] = dir.listFiles();

        if (listFile2 != null) {
            for (int i = 0; i < listFile2.length; i++) {

                if (listFile2[i].isDirectory()) {
                    importWorkoutsHelperDOWNLOAD(listFile2[i]);
                } else {
                    if (listFile2[i].getName().endsWith(".csv")){

                        //Make a list of these and then present in an adb to user
                        String tmp = listFile2[i].getAbsolutePath();
                        arrayOfImports.add(tmp);
                        //Log.d("FOUND", "found a csv file");

                    }
                }
            }
        }

    }

    public void importWorkoutsSelected(String path){

        //Ask to export the current one as a backup
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Backup Current Workouts?");
        builder.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                exportBackup(Calendar.getInstance().getTime().toString() + " Backup");
                dialog.dismiss();

            }

        });

        builder.setNegativeButton("Cancel", null);
        final Dialog dialog = builder.create();
        dialog.show();

        //Import DIRECTLY into Database

        try {
            CSVReader reader = new CSVReader(new FileReader(path), ',');
            String[] record = null;

            //record = reader.readNext();
            if((record = reader.readNext()) != null){
                //make sure it's a workout file first
                if(record[0].equals("Workout")){

                    //Wipe the current table
                    WorkoutTableDb.delete(WorkoutTable.TABLE_NAME, null, null);

                    while ((record = reader.readNext()) != null) {

                        ContentValues values = new ContentValues();
                        values.put(WorkoutTable.WORKOUT_NAME, record[0]);
                        values.put(WorkoutTable.EXERCISE_NAME, record[1]);
                        values.put(WorkoutTable.DATE, record[2]);
                        values.put(WorkoutTable.SET, record[3]);
                        values.put(WorkoutTable.REPS, record[4]);
                        values.put(WorkoutTable.WEIGHT, record[5]);
                        values.put(WorkoutTable.DAYSELECTED, record[6]);

                        WorkoutTableDb.insert(WorkoutTable.TABLE_NAME, null, values);

                    }
                }else{

                    Toast.makeText(getActivity(),"Not a Workout file!", Toast.LENGTH_SHORT).show();

                }

            }




            reader.close();

        }catch(Exception e){
            Log.d("PRINTING STACKTRACE", "Oops");
            e.printStackTrace();
        }



    }

    public void importMeasurementSelected(String path){

        //Import DIRECTLY into Database

        try {
            CSVReader reader = new CSVReader(new FileReader(path), ',');
            String[] record = null;

            if( (record = reader.readNext()) != null){

                //make sure it's a workout file first
                if(record[0].equals("Measurement Type")){

                    //Ask to export the current one as a backup
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Backup Current Measurements?");
                    builder.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            exportBackup(Calendar.getInstance().getTime().toString() + " Backup");
                            dialog.dismiss();

                        }

                    });

                    builder.setNegativeButton("Cancel", null);
                    final Dialog dialog = builder.create();
                    dialog.show();

                    //Wipe the current table
                    MeasurementTableDb.delete(MeasurementTable.TABLE_NAME, null, null);

                    while ((record = reader.readNext()) != null) {

                        ContentValues values = new ContentValues();
                        values.put(MeasurementTable.MEASUREMENT, record[0]);
                        values.put(MeasurementTable.MEASUREMENT_VALUE, record[1]);
                        values.put(MeasurementTable.CALENDAR, record[2]);
                        values.put(MeasurementTable.GOAL_START, record[3]);

                        MeasurementTableDb.insert(MeasurementTable.TABLE_NAME, null, values);

                    }
                }else{

                Toast.makeText(getActivity(),"Not a Measurement file!", Toast.LENGTH_SHORT).show();
                reader.close();

                }

            }


        }catch(Exception e){
            Log.d("PRINTING STACKTRACE", "Oops");
            e.printStackTrace();
        }



    }

    public void addDayToWorkout(){

        AdjustDaysSettings adjustDaysSettingsInstance = new AdjustDaysSettings();
        adjustDaysSettingsInstance.setBoolean(true);

        FragmentManager fragmentManagerWorkoutListView = getActivity().getSupportFragmentManager();
        fragmentManagerWorkoutListView.beginTransaction()
                .replace(R.id.container_settings, adjustDaysSettingsInstance)
                .addToBackStack("Sometag stuff")
                .commit();

    }

    public void removeDayFromWorkout(){

        AdjustDaysSettings adjustDaysSettingsInstance = new AdjustDaysSettings();
        adjustDaysSettingsInstance.setBoolean(false);

        FragmentManager fragmentManagerWorkoutListView = getActivity().getSupportFragmentManager();
        fragmentManagerWorkoutListView.beginTransaction()
                .replace(R.id.container_settings, adjustDaysSettingsInstance)
                .addToBackStack("anothertag stuff")
                .commit();


    }

    public void exportBackup(String filename) {

            //Organize by Workout:Exercise:Date
            //Toast.makeText(getActivity(), "Called export", Toast.LENGTH_SHORT).show();
            SQLiteDatabase WorkoutTableReadable = mDbHelper.getReadableDatabase();

            String[] myProjection = {
                    WorkoutTable.WORKOUT_NAME,
                    WorkoutTable.EXERCISE_NAME,
                    WorkoutTable.DATE,
                    WorkoutTable.SET,
                    WorkoutTable.REPS,
                    WorkoutTable.WEIGHT,
                    WorkoutTable.DAYSELECTED

            };

            //need workoutname parameter in the querey otherwise leg exercies go into chest too
            String selection = WorkoutTable.DATE + " !=? AND "
                    + WorkoutTable.DATE + " !=? AND "
                    + WorkoutTable.DATE + " !=?";

            String[] SelectionArgs = {"0", "1", "2"};

            Cursor tempCur = WorkoutTableReadable.query(
                    WorkoutTable.TABLE_NAME,
                    myProjection,
                    null,
                    null,
                    null,
                    null,
                    null);


            //printDatabase(tempCur);


            try {
                CSVWriter writer;
                //String baseDir = android.os.Environment.getDataDirectory().getAbsolutePath();
                String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
                String filePath = baseDir + File.separator + "MyLiftsData" + File.separator + filename + ".csv";
                File f = new File(baseDir + File.separator + "MyLiftsData");

                if(f.exists() && !f.isDirectory()){
                    //if its exists append
                    FileWriter mFileWriter = new FileWriter(filePath , true);
                    writer = new CSVWriter(mFileWriter, ',');
                    //Log.d("FLAG", "2");
                }

                else if(!f.isDirectory()){
                    //if the directory doesn't exist make it, then export into it
                    f.mkdir();
                    writer = new CSVWriter(new FileWriter(filePath), ',');

                }

                else {
                    //if not make it
                    writer = new CSVWriter(new FileWriter(filePath), ',');
                    //Log.d("FLAG", "3");
                }

                //Log.d("FLAG", "4");

                List<String[]> data = databaseToStringArray(tempCur);
                writer.writeAll(data);
                writer.close();
                Toast.makeText(getActivity(), "Export Successful", Toast.LENGTH_SHORT).show();

            }catch (Exception e){
                Log.d("FAIL", "STACKTRACE FOLLOWING");
                e.printStackTrace();
            }

    }

    public static List<String[]> databaseToStringArray(Cursor tempCur) {
        List<String[]> records2 = new ArrayList<String[]>();
        records2.add(new String[]{"Workout", "Exercise", "Date", "Set", "Reps", "Weight", "DaySelected"});

        tempCur.moveToFirst();
        while (tempCur.moveToNext()) {
            records2.add(new String[]{

                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.EXERCISE_NAME)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.SET)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.REPS)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WEIGHT)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED))

            });
        }

        return records2;
    }

}
