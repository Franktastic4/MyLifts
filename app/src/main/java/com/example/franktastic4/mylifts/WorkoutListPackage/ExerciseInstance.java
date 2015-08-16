package com.example.franktastic4.mylifts.WorkoutListPackage;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;

import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableReaderContract.WorkoutTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.example.franktastic4.mylifts.ListViewRowConstants.FIRST_COLUMN;
import static com.example.franktastic4.mylifts.ListViewRowConstants.FOURTH_COLUMN;
import static com.example.franktastic4.mylifts.ListViewRowConstants.SECOND_COLUMN;
import static com.example.franktastic4.mylifts.ListViewRowConstants.THIRD_COLUMN;



/**
 * Created by Franktastic4 on 5/2/15.
 */
public class ExerciseInstance extends android.support.v4.app.Fragment{


    public ArrayList<ExerciseObject> ExerciseObjectList = new ArrayList<ExerciseObject>();
    public NumberPicker setWheel;
    public NumberPicker weightWheel;
    public NumberPicker repsWheel;

    public ExerciseInstance myExerciseInstance;
    public ListRowAdapterExercise myListRowAdapterExercise;
    private Button exerciseInstanceButton;
    private Button exerciseInstanceUndoButton;
    public boolean itemIsSelected = false;
    public int lastPosition = 0;
    public String exerciseName;
    public String workoutName;

    private boolean lastActionEditBoolean = false;
    private boolean lastActionAddBoolean = false;
    private boolean lastActionDeleteBoolean = false;
    private boolean redoBoolean = false;
    private int undoButtonState = 10;

    private ExerciseObject exerciseObjectBackup;
    private HashMap hashMapBackup;

    WorkoutTableDbHelper mDbHelper;
    static SQLiteDatabase WorkoutTableDb;

    private ArrayList<HashMap> list;

    public static ExerciseInstance newInstance(String exerciseNamePassed, String workoutNamePassed) {
        ExerciseInstance fragment = new ExerciseInstance();
        Bundle args = new Bundle();
        args.putString("exerciseName", exerciseNamePassed);
        args.putString("workoutName", workoutNamePassed);
        fragment.setArguments(args);
        return fragment;
    }

    public ExerciseInstance() {}

    public String return_exercise_name(){

        return getArguments().getString("exerciseName"); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            exerciseName = getArguments().getString("exerciseName");
            workoutName = getArguments().getString("workoutName");
        }

        mDbHelper = new WorkoutTableDbHelper(getActivity().getApplicationContext());
        WorkoutTableDb = mDbHelper.getWritableDatabase();


    }

    @Override
    public void onResume(){
        super.onResume();
        //set title

        ((WorkoutList) getActivity()).setActionBarTitle(exerciseName);
    }

    private void populateList() {

        HashMap temp = new HashMap();
        temp.put(FIRST_COLUMN,"Date");
        temp.put(SECOND_COLUMN, "Set");
        temp.put(THIRD_COLUMN, "Reps");
        temp.put(FOURTH_COLUMN, "Weight");
        list.add(0,temp);

        ExerciseObject myExerciseObject = new ExerciseObject();
        Calendar calendar = Calendar.getInstance();

        myExerciseObject.setDate(calendar);
        myExerciseObject.setSet("null");
        myExerciseObject.setReps("null");
        myExerciseObject.setWeight("null");
        ExerciseObjectList.add(0, myExerciseObject);

    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_exercise_instance, container, false);

        myExerciseInstance = (ExerciseInstance) getActivity().getSupportFragmentManager().findFragmentById(R.id.exercise_Instance_XML);

        ListView lview = (ListView) rootView.findViewById(R.id.ExerciseLogListView);
        list = new ArrayList<HashMap>();
        populateList();
        populateExerciseInstances();

        myListRowAdapterExercise = new ListRowAdapterExercise(getActivity(), list);
        lview.setAdapter(myListRowAdapterExercise);

        //instantiate the wheels
        setWheel = (NumberPicker)rootView.findViewById(R.id.set);
        weightWheel = (NumberPicker)rootView.findViewById(R.id.weight);
        repsWheel = (NumberPicker)rootView.findViewById(R.id.reps);

        setWheel.setMaxValue(200);
        setWheel.setMinValue(0);
        setWheel.setWrapSelectorWheel(false);

        repsWheel.setMaxValue(200);
        repsWheel.setMinValue(0);
        repsWheel.setWrapSelectorWheel(false);

        String[] tmpStringArray = fillArray();
        weightWheel.setMaxValue(tmpStringArray.length - 1);
        weightWheel.setMinValue(0);
        weightWheel.setDisplayedValues(tmpStringArray);
        weightWheel.setWrapSelectorWheel(false);

        //log button
        exerciseInstanceButton = (Button) rootView.findViewById(R.id.logExerciseInstanceButton);
        exerciseInstanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logExerciseInstanceDone();
            }
        });

        exerciseInstanceUndoButton = (Button) rootView.findViewById(R.id.logExerciseInstanceUndo);
        exerciseInstanceUndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logExerciseInstanceUndo();
            }
        });

        //curMonth just happens to be there for this example so just make an instance of it.

        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    exerciseInstanceButton.setText("Update");
                    exerciseInstanceUndoButton.setText("Delete");
                    itemIsSelected = true;
                    lastPosition = position;
                }else{
                    exerciseInstanceButton.setText("Log Instance");
                    exerciseInstanceUndoButton.setText("Undo");
                    itemIsSelected = false;
                }

            }

            });

                return rootView;
    }

    public void populateExerciseInstances(){

        SQLiteDatabase WorkoutTableReadable  = new WorkoutTableDbHelper(getActivity().getApplicationContext()).getReadableDatabase();

        String[] myProjection = {
                WorkoutTable.WORKOUT_NAME,
                WorkoutTable.EXERCISE_NAME,
                WorkoutTable.DATE,
                WorkoutTable.SET,
                WorkoutTable.REPS,
                WorkoutTable.WEIGHT
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection = WorkoutTable.WORKOUT_NAME + "=? AND "
                + WorkoutTable.EXERCISE_NAME + "=? AND "
                + WorkoutTable.DATE  + " !=? AND "
                + WorkoutTable.DATE + " !=? AND "
                + WorkoutTable.DATE + " !=?";

        String[] SelectionArgs = {workoutName,exerciseName ,"1", "2", "3"};

        Cursor tempCur = WorkoutTableReadable.query(
                WorkoutTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

        if(tempCur.moveToFirst()){

            ExerciseObject myExerciseObject = new ExerciseObject();

            String dateString = tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE));
            Calendar cal = Calendar.getInstance();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                cal.setTime(sdf.parse(dateString));
            } catch (java.text.ParseException e) {

            }

            //Log.d("FLAG", "Populating this: " + cal.toString());


            //A calender must go in
            myExerciseObject.setDate(cal);
            myExerciseObject.setSet(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.SET)));
            myExerciseObject.setReps(tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.REPS)));
            myExerciseObject.setWeight(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WEIGHT)));
            ExerciseObjectList.add(1, myExerciseObject);

            HashMap temp = new HashMap();
            temp.put(FIRST_COLUMN, myExerciseObject.returnMonth() + "/" + myExerciseObject.returnDay() );
            temp.put(SECOND_COLUMN, myExerciseObject.returnSet());
            temp.put(THIRD_COLUMN, myExerciseObject.returnReps());
            temp.put(FOURTH_COLUMN, myExerciseObject.returnWeight());
            list.add(1, temp);

        }

        while(tempCur.moveToNext()) {

            ExerciseObject myExerciseObject = new ExerciseObject();
            String dateString = tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.DATE));

            Calendar cal = Calendar.getInstance();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                cal.setTime(sdf.parse(dateString));
            } catch (java.text.ParseException e) {

            }

           // Log.d("FLAG", "Populating this: " + cal.toString());

            //A calender must go in
            myExerciseObject.setDate(cal);
            myExerciseObject.setSet(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.SET)));
            myExerciseObject.setReps(tempCur.getString(tempCur.getColumnIndex(WorkoutTableReaderContract.WorkoutTable.REPS)));
            myExerciseObject.setWeight(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WEIGHT)));
            ExerciseObjectList.add(1,myExerciseObject);

            HashMap temp = new HashMap();
            temp.put(FIRST_COLUMN, myExerciseObject.returnMonth() + "/" + myExerciseObject.returnDay() );
            temp.put(SECOND_COLUMN, myExerciseObject.returnSet());
            temp.put(THIRD_COLUMN, myExerciseObject.returnReps());
            temp.put(FOURTH_COLUMN, myExerciseObject.returnWeight());
            list.add(1,temp);

        }
    }

    public void logExerciseInstanceDone() {

                if(itemIsSelected) {

                    if(lastPosition == 0){

                        itemIsSelected = false;
                        exerciseInstanceButton.setText(R.string.add_exerciseInstance_done);

                    }else {

                        lastActionEdit();
                        exerciseObjectBackup = ExerciseObjectList.get(lastPosition);
                        ExerciseObject updateObj = ExerciseObjectList.get(lastPosition);
                        hashMapBackup = list.get(lastPosition);

                        //Using this chunk because exerciseObjectBackup's values change, dunno why it actually works later...?
                        final Calendar finalCalendar = updateObj.returnTimeStamp();
                        final String finalSet = updateObj.returnSet();
                        final String finalReps = updateObj.returnReps();
                        final String finalWeight = updateObj.returnWeight();

                        ExerciseObject ExerciseObjectTemp = new ExerciseObject();
                        ExerciseObjectTemp.setDate(finalCalendar);
                        ExerciseObjectTemp.setSet(finalSet);
                        ExerciseObjectTemp.setReps(finalReps);
                        ExerciseObjectTemp.setWeight(finalWeight);


                        updateObj.setSet(Integer.toString(setWheel.getValue()));
                        updateObj.setReps(Integer.toString(repsWheel.getValue()));
                        updateObj.setWeight(Integer.toString(weightWheel.getValue()));
                        ExerciseObjectList.set(lastPosition, updateObj);

                        HashMap temp = new HashMap();
                        //Don't change the time
                        temp.put(FIRST_COLUMN, finalCalendar.MONTH + "/" + finalCalendar.DAY_OF_MONTH);
                        temp.put(SECOND_COLUMN, updateObj.returnSet());
                        temp.put(THIRD_COLUMN, updateObj.returnReps());
                        temp.put(FOURTH_COLUMN, updateObj.returnWeight());
                        list.set(lastPosition, temp);

                        //printDatabase();
                        updateDatabase(updateObj, ExerciseObjectTemp, null);
                        //printDatabase();

                        exerciseInstanceButton.setText(R.string.add_exerciseInstance_done);
                        itemIsSelected = false;
                        exerciseInstanceUndoButton.setText("Undo Edit");

                        myListRowAdapterExercise.notifyDataSetChanged();

                    }

                }else{

                    ExerciseObject myExerciseObject = new ExerciseObject();
                    Calendar calendar = Calendar.getInstance();

                    //Date myDate = calendar.getTime();
                    //calendar.setTime(myDate);

                    myExerciseObject.setDate(calendar);

                    myExerciseObject.setSet(Integer.toString(setWheel.getValue()));
                    myExerciseObject.setReps(Integer.toString(repsWheel.getValue()));
                    myExerciseObject.setWeight(Integer.toString(weightWheel.getValue()));

                    ExerciseObjectList.add(1,myExerciseObject);
                    setWheel.setValue(setWheel.getValue() + 1);

                    //date too
                    HashMap temp = new HashMap();
                    temp.put(FIRST_COLUMN, myExerciseObject.returnMonth() + "/" + myExerciseObject.returnDay());
                    temp.put(SECOND_COLUMN, myExerciseObject.returnSet());
                    temp.put(THIRD_COLUMN, myExerciseObject.returnReps());
                    temp.put(FOURTH_COLUMN, myExerciseObject.returnWeight());
                    list.add(1, temp);

                    addDatabase(myExerciseObject);
                    //printDatabase();

                    lastPosition = list.indexOf(temp);
                    exerciseInstanceUndoButton.setText("Undo Add");
                    lastActionAdd();
                    undoButtonState = 3;

                    myListRowAdapterExercise.notifyDataSetChanged();

                }

            //printDatabase();
            }

    public void logExerciseInstanceUndo() {

        if(itemIsSelected) {

            //DELETE BUTTON
            //When item is selected, can only be DELETE

            //Delete in progress
            exerciseInstanceButton.setText(R.string.add_exerciseInstance_done);

            if(lastPosition == 0){

                itemIsSelected = false;

            }else {
                //Store a copy for undo
                exerciseObjectBackup = ExerciseObjectList.get(lastPosition);
                hashMapBackup = list.get(lastPosition);
                lastActionDelete();

                //DELETE
                list.remove(lastPosition);
                removeDatabase(ExerciseObjectList.get(lastPosition), false);
                ExerciseObjectList.remove(lastPosition);

                exerciseInstanceUndoButton.setText("Undo Delete");
                undoButtonState = 1;
                redoBoolean = false;
                itemIsSelected = false;

                myListRowAdapterExercise.notifyDataSetChanged();

            }

        }else{

            // The right button is tapped with a selection
            // Can only be Undo or Redo
            //UNDO BUTTON (Include Redo instead of multiple UNDOs)

            if(lastActionAddBoolean){

                //UNDO ADD
                //Store a copy for redo

                exerciseObjectBackup = ExerciseObjectList.get(lastPosition);
                hashMapBackup = list.get(lastPosition);

                list.remove(lastPosition);
                removeDatabase(ExerciseObjectList.get(lastPosition), false);
                ExerciseObjectList.remove(lastPosition);

                myListRowAdapterExercise.notifyDataSetChanged();
                lastActionDelete();

                if(undoButtonState == 2){
                    undoButtonState = 1;
                    exerciseInstanceUndoButton.setText("Undo Delete");
                }else if(undoButtonState == 3){
                    undoButtonState = 4;
                    exerciseInstanceUndoButton.setText("Redo Add");

                }

            }else if(lastActionDeleteBoolean){
                //add
                //UNDO DELETE

                ExerciseObjectList.add(lastPosition, exerciseObjectBackup);
                list.add(lastPosition, hashMapBackup);
                removeDatabase(exerciseObjectBackup, true);
                myListRowAdapterExercise.notifyDataSetChanged();
                lastActionAdd();

                if(undoButtonState == 1){
                    //Delete sets state to 1
                    //Undo button pressed
                    //Added Something
                    undoButtonState = 2;
                    exerciseInstanceUndoButton.setText("Redo Delete");
                }else if(undoButtonState == 4){
                    undoButtonState = 3;
                    exerciseInstanceUndoButton.setText("Undo Add");
                }

            }else if(lastActionEditBoolean){
                //restore

                //Store Current Value
                ExerciseObject ExerciseObjectBackupTemp = ExerciseObjectList.get(lastPosition);
                HashMap hashMapBackupTemp = list.get(lastPosition);

                // STORING OLD OBJECT
                final Calendar finalCalendar = ExerciseObjectBackupTemp.returnTimeStamp();
                final String finalSet = ExerciseObjectBackupTemp.returnSet();
                final String finalReps = ExerciseObjectBackupTemp.returnReps();
                final String finalWeight = ExerciseObjectBackupTemp.returnWeight();

                ExerciseObject ExerciseObjectTemp = new ExerciseObject();
                ExerciseObjectTemp.setDate(finalCalendar);
                ExerciseObjectTemp.setSet(finalSet);
                ExerciseObjectTemp.setReps(finalReps);
                ExerciseObjectTemp.setWeight(finalWeight);
                // END STORING OLD OBJECT

                //Restore Value as intended
                ExerciseObjectList.set(lastPosition, exerciseObjectBackup);
                list.set(lastPosition, hashMapBackup);

                // new old null
                updateDatabase(exerciseObjectBackup, ExerciseObjectTemp, null);

                myListRowAdapterExercise.notifyDataSetChanged();

                //Set the value we replaced into the temps as backups
                exerciseObjectBackup = ExerciseObjectBackupTemp;
                hashMapBackup = hashMapBackupTemp;
                lastActionEdit();

                if(redoBoolean) {
                    exerciseInstanceUndoButton.setText("Undo Update");
                    redoBoolean = false;
                }else{
                    exerciseInstanceUndoButton.setText("Redo Update");
                    redoBoolean = true;
                }
            }

        }

        //printDatabase();
    }

    public void addDatabase(ExerciseObject temp){

        ContentValues values = new ContentValues();
        values.put(WorkoutTable.WORKOUT_NAME, workoutName);
        values.put(WorkoutTable.EXERCISE_NAME , exerciseName);
        values.put(WorkoutTable.DATE, temp.returnTimeStamp().getTime().toString());
        values.put(WorkoutTable.SET, temp.returnSet());
        values.put(WorkoutTable.REPS, temp.returnReps());
        values.put(WorkoutTable.WEIGHT, temp.returnWeight());
        WorkoutTableDb.insert(WorkoutTable.TABLE_NAME, null, values);

        //printDatabase();
    }

    public void removeDatabase(ExerciseObject oldObj, Boolean undo){

        //Change date to 3, so we don't have to worry about messing up order later in the DB

        final Calendar setDate = oldObj.returnTimeStamp();
        final String setString = oldObj.returnSet();
        final String repsString = oldObj.returnReps();
        final String weightString = oldObj.returnWeight();

        ExerciseObject newObj = new ExerciseObject();
        newObj.setDate(setDate);
        newObj.setSet(setString);
        newObj.setReps(repsString);
        newObj.setWeight(weightString);

        if(undo){
            updateDatabase(newObj, oldObj, oldObj.returnTimeStamp().getTime().toString());

        }else{
            updateDatabase(newObj, oldObj, "3");

        }


    }

    public void updateDatabase(ExerciseObject newObj, ExerciseObject oldObj, String deletedString){

        ContentValues values = new ContentValues();
        values.put(WorkoutTable.WORKOUT_NAME, workoutName);
        values.put(WorkoutTable.EXERCISE_NAME, exerciseName);
        values.put(WorkoutTable.SET, newObj.returnSet());
        values.put(WorkoutTable.REPS, newObj.returnSet());
        values.put(WorkoutTable.WEIGHT, newObj.returnWeight());

        if(deletedString == null) {
            values.put(WorkoutTable.DATE, newObj.returnTimeStamp().getTime().toString());
        }else{
            values.put(WorkoutTable.DATE, deletedString);
        }

        //What if I did the same exercise twice in a workout? Warn them about using same set number?
        //calender knows seconds

        String selection =
                WorkoutTable.WORKOUT_NAME + " =? AND "
                        + WorkoutTable.EXERCISE_NAME + " =? AND "
                        + WorkoutTable.DATE + " =? AND "
                        + WorkoutTable.SET + " =? AND "
                        + WorkoutTable.REPS + " =? AND "
                        + WorkoutTable.WEIGHT + " =?"
                ;

        //selectionargs tell us what the old one is SUPPOSE to look like. Now how do we retrieve the
        //date from the database, and then compare to find the right row?

        // instead of comparing the selectionargs to Calendar.getTime().toString(), I will compare it
        // directly to Calender.toString() as I assume its stored inside the database

        String[] selectionargs = {
                workoutName,
                exerciseName,
                oldObj.returnTimeStamp().getTime().toString(),
                oldObj.returnSet(),
                oldObj.returnReps(),
                oldObj.returnWeight()
        };


        WorkoutTableDb.update(
                WorkoutTable.TABLE_NAME,
                values,
                selection,
                selectionargs
        );


    }

    public void lastActionEdit(){
        lastActionAddBoolean = lastActionDeleteBoolean = false;
        lastActionEditBoolean = true;
    }

    public void lastActionDelete(){
        lastActionAddBoolean = lastActionEditBoolean = false;
        lastActionDeleteBoolean = true;
    }

    public void lastActionAdd(){
        lastActionDeleteBoolean = lastActionEditBoolean = false;
        lastActionAddBoolean = true;
    }

    public String[] fillArray(){

         return new String[]{

                "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60", "65", "70", "75", "80", "85", "90", "95", "100", "105", "110", "115", "120", "125",
                "130", "135", "140", "145", "150", "155", "160", "165", "170", "175", "180", "185", "190", "195", "200", "205", "210", "215", "220", "225", "230", "235", "240", "245", "250",
                "255", "260", "265", "270", "275", "280", "285", "290", "295", "300", "305", "310", "315", "320", "325", "330", "335", "340", "345", "350", "355", "360", "365", "370", "375",
                "380", "385", "390", "395", "400", "410", "420", "430", "440", "450", "460", "470", "480", "490", "500",
                "510", "520", "530", "540", "550", "560", "570", "580", "590", "600", "610", "620", "630", "640", "650", "660", "670", "680", "690", "700", "710", "720", "730", "740", "750",
                "760", "770", "780", "790", "800", "810", "820", "830", "840", "850", "860", "870", "880", "890", "900", "910", "920", "930", "940", "950", "960", "970", "980", "990", "1000",
                "1025", "1050", "1075", "1100", "1125", "1150", "1175", "1200", "1225", "1250", "1275", "1300", "1325", "1350", "1375", "1400", "1425", "1450", "1475", "1500"
                //, "1525", "1550", "1575", "1600", "1625", "1650", "1675", "1700", "1725", "1750", "1775", "1800", "1825", "1850", "1875",
                //"1900", "1925", "1950", "1975", "2000", "2025", "2050", "2075", "2100", "2125", "2150", "2175", "2200", "2225", "2250", "2275", "2300", "2325", "2350", "2375", "2400", "2425", "2450", "2475", "2500",

        };


    }

}