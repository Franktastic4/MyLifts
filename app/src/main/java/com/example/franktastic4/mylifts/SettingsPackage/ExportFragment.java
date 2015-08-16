package com.example.franktastic4.mylifts.SettingsPackage;

/**
 * Created by Franktastic4 on 7/16/15.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableDbHelper;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableReaderContract.MeasurementTable;
import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableDbHelper;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableReaderContract.WorkoutTable;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExportFragment extends Fragment {

    private ArrayAdapter<String> exportListArrayAdapter;
    private WorkoutTableDbHelper mDbHelper;
    SQLiteDatabase WorkoutTableReadable;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new WorkoutTableDbHelper(getActivity().getApplicationContext());
        WorkoutTableReadable = mDbHelper.getReadableDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_export, container, false);

        exportListArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                new String[]{
                        "Export All",
                        "Export Workouts",
                        "Export Measurements",
                        "Export Journal",
                        "Export Analysis"
                }
        );

        ListView exportListView = (ListView) rootView.findViewById(R.id.exportListView);
        exportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                exportItemSelected(position);
            }
        });

        exportListView.setAdapter(exportListArrayAdapter);


        return rootView;
    }

    public void exportItemSelected(final int index) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText edittext = new EditText(getActivity());
        alert.setMessage("Name the file you are exporting");
        alert.setTitle("Name File");
        alert.setView(edittext);

        alert.setPositiveButton("Export", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (edittext.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Name is empty, Export Failed", Toast.LENGTH_SHORT).show();
                } else {
                    exportBegin(edittext.getText().toString(), index);
                }
            }
        });
        alert.setNegativeButton("Cancel", null);
        alert.show();
    }

    public void exportBegin(String filename, int index) {


        switch (index) {
            case 0: //Export All
                exportAll(filename);
                break;

            case 1: //Export Workouts
                exportWorkoutTable(filename);
                break;

            case 2: //Exports Measurements
                exportMeasurementsTable(filename);
                break;

            case 3: //Exports Journal

                break;

            case 4: //Exports Analysis

                break;

            default:
                Toast.makeText(getActivity(), "How did you pick this?", Toast.LENGTH_LONG).show();
                break;
        }

    }

    private void exportWorkoutTable(String filename) {

        //Organize by Workout:Exercise:Date
        //Toast.makeText(getActivity(), "Called export", Toast.LENGTH_SHORT).show();

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

    public File directoryCheck(String filename){

        File directory = null;

        //if there is no SD card, create new directory objects to make directory on device
        if (Environment.getExternalStorageState() == null) {
            //create new file directory object
             directory = new File(Environment.getDataDirectory()
                    + "/" + filename + "/");
            /*
             * this checks to see if there are any previous test photo files
             * if there are any photos, they are deleted for the sake of
             * memory
             */
            if (directory.exists()) {
                File[] dirFiles = directory.listFiles();
                if (dirFiles.length != 0) {
                    for (int ii = 0; ii <= dirFiles.length; ii++) {
                        dirFiles[ii].delete();
                    }
                }
            }
            // if no directory exists, create new directory
            if (!directory.exists()) {
                directory.mkdir();
            }

            // if phone DOES have sd card
        } else if (Environment.getExternalStorageState() != null) {
            // search for directory on SD card
            directory = new File(Environment.getExternalStorageDirectory()
                    + "/MyLiftsData/");

            if (directory.exists()) {
                File[] dirFiles = directory.listFiles();
                if (dirFiles.length > 0) {
                    for (int ii = 0; ii < dirFiles.length; ii++) {
                        dirFiles[ii].delete();
                    }
                    dirFiles = null;
                }
            }
            // if no directory exists, create new directory to store test
            // results
            if (!directory.exists()) {
                directory.mkdir();
            }
        }// end of SD card checking

        return directory;
    }

    public void printDatabase(Cursor tempCur){

        if(tempCur.moveToFirst()){

            Log.d("TABLE ITEM", tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.EXERCISE_NAME))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.SET))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.REPS))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WEIGHT))
            );

        }


        while(tempCur.moveToNext()){

            Log.d("TABLE ITEM", tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.EXERCISE_NAME))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.SET))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.REPS))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WEIGHT))
            );

        }
    }

    public static List<String[]> databaseToStringArray(Cursor tempCur) {
        List<String[]> records = new ArrayList<String[]>();
        records.add(new String[]{"Workout", "Exercise", "Date", "Set", "Reps", "Weight", "DaySelected"});

        if(tempCur.moveToFirst()){
            records.add(new String[]{

                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.EXERCISE_NAME)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.SET)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.REPS)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WEIGHT)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED))

            });

        }



        while (tempCur.moveToNext()) {
            records.add(new String[]{

                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.EXERCISE_NAME)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.SET)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.REPS)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WEIGHT)),
                    tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DAYSELECTED))

            });
        }

        return records;
    }

    private void exportMeasurementsTable(String filename) {

        //Organize by Measurement:Date

        MeasurementTableDbHelper mDbHelper = new MeasurementTableDbHelper(getActivity().getApplicationContext());
        ;
        SQLiteDatabase MeasurementTableReadable = mDbHelper.getReadableDatabase();

        String[] myProjection = {
                MeasurementTable.MEASUREMENT,
                MeasurementTable.MEASUREMENT_VALUE,
                MeasurementTable.CALENDAR,
                MeasurementTable.GOAL_START
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
//        String selection = MeasurementTable.CALENDAR + " !=? AND "
//                + MeasurementTable.CALENDAR + " !=? AND "
//                + MeasurementTable.CALENDAR + " !=?";
//
//        String[] SelectionArgs = {"0", "1", "2"};

        String selection = MeasurementTable.CALENDAR + " !=? AND "
                + MeasurementTable.CALENDAR + " !=?";

        String[] SelectionArgs = {"0","2"};

        Cursor tempCur = MeasurementTableReadable.query(
                MeasurementTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

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

            List<String[]> data = databaseToStringArrayMeasurement(tempCur);
            writer.writeAll(data);
            writer.close();
            Toast.makeText(getActivity(), "Export Successful", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Log.d("FAIL", "STACKTRACE FOLLOWING");
            e.printStackTrace();
        }


    }

    List<String[]> databaseToStringArrayMeasurement(Cursor tempCur){

        List<String[]> records = new ArrayList<String[]>();
        records.add(new String[]{"Measurement Type", "Measurement", "Date", "Goal?"});

        if(tempCur.moveToFirst()){
            records.add(new String[]{

                    tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT)),
                    tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE)),
                    tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR)),
                    tempCur.getString(tempCur.getColumnIndex(MeasurementTable.GOAL_START)),


            });

        }

        while (tempCur.moveToNext()) {
            records.add(new String[]{

                    tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT)),
                    tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE)),
                    tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR)),
                    tempCur.getString(tempCur.getColumnIndex(MeasurementTable.GOAL_START)),


            });
        }

        return records;

    }

    public void exportAll(String filename){

        exportWorkoutTable(filename + " Workouts " + Calendar.getInstance().getTime().toString());
        exportMeasurementsTable(filename + " Measurements " + Calendar.getInstance().getTime().toString());

    }

}




