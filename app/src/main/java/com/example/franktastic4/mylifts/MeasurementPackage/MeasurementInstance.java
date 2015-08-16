package com.example.franktastic4.mylifts.MeasurementPackage;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableReaderContract.MeasurementTable;
import com.example.franktastic4.mylifts.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.example.franktastic4.mylifts.ListViewRowConstants.FIRST_COLUMN;
import static com.example.franktastic4.mylifts.ListViewRowConstants.SECOND_COLUMN;
import static com.example.franktastic4.mylifts.ListViewRowConstants.THIRD_COLUMN;

/**
 * Created by Franktastic4 on 6/29/15.
 */
public class MeasurementInstance extends android.support.v4.app.Fragment{

    private Calendar date;
    private String measurementName;
    private ArrayList<HashMap> list;
    private ArrayList<HashMap> oldList;
    public ListRowAdapterMeasurement myListRowAdapterMeasurement;
    private ArrayList<MeasurementObject> measurementObjectsArray = new ArrayList<MeasurementObject>();
    private Button doneButton;
    private Button undoButton;
    private Button incrementButton;
    private Button decrementButton;
    private EditText editText;
    private TextView goalTextView;
    private HashMap hashMapbackup;
    private MeasurementObject measurementObjectBackup;
    private MeasurementObject baseGoalMeasurement = new MeasurementObject();
    private ListView measurementListView;
    private MeasurementInstance myMeasurementsInstance;

    public boolean thirdColumnBoolean = false;
    private int lastPosition = 0;
    private boolean isItemSelectedBoolean = false;
    private int currentEditTextValue = 0;
    private long timeDiff = 0;
    private double goalDiffGain = 0;
    private double goalDiffLoss = 0;
    private double goalDiffGainNOCHANGE = 0;
    private double goalDiffLossNOCHANGE = 0;
    private boolean lastActionEditBoolean = false;
    private boolean lastActionAddBoolean = false;
    private boolean lastActionDeleteBoolean = false;
    private int stateVar = 99;
    private double initialValue = 0;
    private boolean goalIsActive = false;
    private int myGoalCalendar = 0;
    private double myGoalValue = 0;
    private boolean goalIsToLoseWeight = false;
    private Boolean copyList = false;

    MeasurementTableDbHelper mDbHelper;
    SQLiteDatabase MeasurementTableDB;

    public void setGoalIsToLoseWeight(boolean newBool){goalIsToLoseWeight = newBool;}
    public void setMyGoalValue(double newGoalValue){myGoalValue = newGoalValue;}

    public void setMyGoalCalendar(int newGoalCal){
        myGoalCalendar = newGoalCal;
    }


    public String return_measurement_instance_name(){return measurementName;}
    public void setMeasurementInstanceName(String name){ measurementName = name;}
    public void setDate(Calendar dateNew){date = dateNew;}
    public final String GOAL_START = "Started Goal";

    public static MeasurementInstance newInstance(String newMeasurementName) {
        MeasurementInstance fragment = new MeasurementInstance();
        Bundle args = new Bundle();
        args.putString("measurementName", newMeasurementName);
        fragment.setArguments(args);
        return fragment;
    }

    public MeasurementInstance(){}

    @Override
    public void onResume(){
        super.onResume();
        ((MeasurementsActivity)getActivity()).setActionBarTitle(measurementName + " Measurements");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MeasurementsFragment myMeasurementFragment = (MeasurementsFragment)getFragmentManager().findFragmentByTag("measurements_fragment_tag");
        measurementName = myMeasurementFragment.measurementInstanceArray.get(myMeasurementFragment.lastPosition).return_measurement_instance_name();
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(measurementName + " Measurements");

        mDbHelper = new MeasurementTableDbHelper(getActivity().getApplicationContext());
        MeasurementTableDB = mDbHelper.getWritableDatabase();

    }

    private void populateList(){

        if(list.isEmpty()){

            MeasurementObject firstMeasurementObject = new MeasurementObject();
            measurementObjectsArray.add(0, firstMeasurementObject);

            HashMap temp = new HashMap();
            temp.put(FIRST_COLUMN, "Date");
            temp.put(SECOND_COLUMN, "Measurement");
            temp.put(THIRD_COLUMN, "Current Rate");
            list.add(0, temp);

            populateFromDatabase();

            myListRowAdapterMeasurement.notifyDataSetChanged();
        }
    }

    public void populateFromDatabase(){

        SQLiteDatabase MeasurementTableReadable  = new MeasurementTableDbHelper(getActivity().getApplicationContext()).getReadableDatabase();

        String[] myProjection = {
                MeasurementTable.MEASUREMENT,
                MeasurementTable.MEASUREMENT_VALUE,
                MeasurementTable.CALENDAR,
                MeasurementTable.GOAL_START
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection =
                MeasurementTable.MEASUREMENT + "=? AND "
                        + MeasurementTable.CALENDAR + " !=? AND "
                        + MeasurementTable.CALENDAR  + " !=?";

        String[] SelectionArgs = {measurementName ,"1","0"};

        Cursor tempCur = MeasurementTableReadable.query(
                MeasurementTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);


        //If there is a goal, look for it
        //if(goalIsActive) {

            //if there is a list
            if (tempCur.moveToFirst()) {
                //Did I find the a goal?
                findBaseGoal(tempCur);
            }

            //keep trying
            while (tempCur.moveToNext()) {
                findBaseGoal(tempCur);
            }
        //}


        //Actual populating list
        if(tempCur.moveToFirst()) {
            populateListHelper(tempCur);
        }

        while(tempCur.moveToNext()) {
            populateListHelper(tempCur);
        }

    }

    private double calculateRate(MeasurementObject currentMeasurementObj, MeasurementObject baseGoalMeasurement){


        if(currentMeasurementObj == null){
            Log.d("TAGG", "CurrMeasObj");
        }else if(baseGoalMeasurement == null){
            Log.d("TAGG", "baseGoal");
        }

        double timeDiffVar = (baseGoalMeasurement.returnTimeStamp().getTimeInMillis() - currentMeasurementObj.returnTimeStamp().getTimeInMillis()) / 86400000;
        double currentRate = ((myGoalValue - initialValue)/timeDiffVar);

        //TODO - not working!!!
        if(timeDiffVar < .000694){
            return -1;
        }else if(currentRate < 0){
            //absolute value
            return -1*currentRate;
        }else {
            return currentRate;
        }
    }

    private void populateListHelper(Cursor tempCur) {

        SharedPreferences sharedPrefActive = getActivity().getSharedPreferences(getString(R.string.GoalIsActive), Context.MODE_PRIVATE);

        if(!tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR)).equals("0")) {

            MeasurementObject myMeasurementObject = new MeasurementObject();

            String dateString = tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR));
            Calendar cal = Calendar.getInstance();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                cal.setTime(sdf.parse(dateString));
            } catch (java.text.ParseException e) {

            }

            //A calender must go in
            myMeasurementObject.setTimeStamp(cal);
            myMeasurementObject.setMeasurmentValue(Double.parseDouble(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE))));

            String comparingString = tempCur.getString(tempCur.getColumnIndex(MeasurementTable.GOAL_START));
            if (comparingString.equals(GOAL_START)) {
                myMeasurementObject.setIsGoalStart(true);
            } else {
                myMeasurementObject.setIsGoalStart(false);
            }

            measurementObjectsArray.add(1, myMeasurementObject);

            HashMap temp = new HashMap();
            temp.put(FIRST_COLUMN, myMeasurementObject.returnMonth() + "/" + myMeasurementObject.returnDay());
            temp.put(SECOND_COLUMN, myMeasurementObject.returnMeasurementValue());

            double currentRate;
            //Doesn't this imply there is a goal, so we must have found it
            if (sharedPrefActive.getBoolean(getString(R.string.GoalIsActive),false)) {
                //there is a goal, calculate rate

                if (myMeasurementObject.isGoalStart()) {
                    temp.put(THIRD_COLUMN, GOAL_START);

                } else {

                    currentRate = calculateRate(myMeasurementObject, baseGoalMeasurement);

                    if (currentRate == -1) {
                        //Too Soon
                        temp.put(THIRD_COLUMN, "Too Soon");
                    } else {
                        temp.put(THIRD_COLUMN, currentRate);
                    }
                }
            } else {
                //No Goal
                temp.put(THIRD_COLUMN, "");
            }

            list.add(1, temp);
        }
}

    private void findBaseGoal(Cursor tempCur){

        //if goal is found
        if (tempCur.getString(tempCur.getColumnIndex(MeasurementTable.GOAL_START)).equals(GOAL_START)) {

            //set up the baseGoalMeasurements
            //Log.d("FLAG", "FOUND GOAL");

            //Don't need to change remakeFrag changes this value
            goalIsActive = true;


            MeasurementObject thisMesObj = new MeasurementObject();

            String dateString = tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR));
            Calendar cal = Calendar.getInstance();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                cal.setTime(sdf.parse(dateString));
            } catch (java.text.ParseException e) {

            }



            thisMesObj.setTimeStamp(cal);
            thisMesObj.setMeasurmentValue(Double.parseDouble(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE))));
            thisMesObj.setIsGoalStart(true);

            baseGoalMeasurement = thisMesObj;

            if(!thirdColumnBoolean){
                remakeFrag(true);
            }

        }

    }

    private void remakeFrag(Boolean nextBool){

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.GoalIsActive), Context.MODE_PRIVATE);
        SharedPreferences sharedPrefTimeValues = getActivity().getSharedPreferences(getString(R.string.TimeVarSet), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.GoalIsActive), nextBool);
        editor.apply();

        if(!nextBool){
            SharedPreferences.Editor editor2 = sharedPrefTimeValues.edit();
            editor2.putLong(getString(R.string.TimeVar1), -1);
            editor2.putLong(getString(R.string.TimeVar2), -1);
            editor2.putLong(getString(R.string.TimeVar3), -1);
            editor2.putLong(getString(R.string.TimeVar4), -1);
            editor2.apply();
        }

        oldList = list;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MeasurementInstance newFragment = new MeasurementInstance();
        newFragment.thirdColumnBoolean = nextBool;
        newFragment.oldList = this.list;
        newFragment.measurementObjectsArray = measurementObjectsArray;

        copyList = true;
        newFragment.copyList = copyList;

        newFragment.goalIsActive = nextBool;
        newFragment.baseGoalMeasurement = baseGoalMeasurement;
        newFragment.myGoalValue = myGoalValue;
        newFragment.initialValue = initialValue;
        newFragment.goalIsToLoseWeight = goalIsToLoseWeight;
        newFragment.myGoalCalendar = myGoalCalendar;
        newFragment.goalTextView = goalTextView;

        //To show textView, must give it data

        getActivity().getSupportFragmentManager().popBackStack();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container_measurements, newFragment, "measurements_instance_tag");
        transaction.addToBackStack("measurement_fragment_tag");
        transaction.commit();
    }

    public void resetThirdColumn(){


        //Recalculate currentRate every time we open, first DETECT THE GOAL from database.
        //use calculateRate(measurementObject, goalObject)

        //LIST
        //need more than just the goal to begin wiping
        if(list.size() > 2) {

            // Index 1 is the goal
            for (int index = 2; index < list.size(); index++) {
                HashMap temp = list.get(index);
                //MeasurementObject tempOther = measurementObjectsArray.get(index);
                temp.put(THIRD_COLUMN, "");
                list.set(index, temp);
            }
        }

    }

    public void addMeasurementObject(Double currentValue) {

        //this is add goal
        //updates goals when the onCreateView realizes we remake the view b/c of the remakeFrag/Goal

        if(!goalIsActive) {

            //Wipe out 3rd column first in case this is a re-add
            initialValue = currentValue;
            baseGoalMeasurement.setMeasurmentValue(currentValue);
            baseGoalMeasurement.setTimeStamp(Calendar.getInstance());
            baseGoalMeasurement.setIsGoalStart(true);
            measurementObjectsArray.add(1, baseGoalMeasurement);

            resetThirdColumn();

            HashMap temp = new HashMap();
            temp.put(FIRST_COLUMN, baseGoalMeasurement.returnMonth() + "/" + baseGoalMeasurement.returnDay());
            temp.put(SECOND_COLUMN, baseGoalMeasurement.returnMeasurementValue());
            temp.put(THIRD_COLUMN, GOAL_START);
            list.add(1, temp);


            addDatabase(baseGoalMeasurement);

            isItemSelectedBoolean = false;
            thirdColumnBoolean = true;
            goalIsActive = true;

            //refreshes the onCreateView
            //TODO, if you remove the goal, back to two columns
            remakeFrag(true);
            myListRowAdapterMeasurement.notifyDataSetChanged();

            //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //SharedPreferences.Editor editor = settings.edit();
            //editor.putBoolean("ThirdColumnKey", true);
            //editor.commit();

        }else{
            Toast.makeText(getActivity(), "One goal at a time!", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_measurement_instance, container, false);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.GoalViewKey), Context.MODE_PRIVATE);
        SharedPreferences sharedPrefActive = getActivity().getSharedPreferences(getString(R.string.GoalIsActive), Context.MODE_PRIVATE);

        //Log.d("Test", "Boolean is " + sharedPref.getBoolean(getString(R.string.GoalViewKey),false) );

        myMeasurementsInstance = (MeasurementInstance)getFragmentManager().findFragmentByTag("measurements_instance_tag");
        list = new ArrayList<HashMap>();

        if(copyList){
            list = oldList;
        }

        //Shared Pref -- Is there a goal active. We know if we just switched, but not if we are reaccessing
        //3rd Column made in the adapter, happens before populateList, where we search for the Goal itself.
        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //thirdColumnBoolean = settings.getBoolean("ThirdColumnKey", false);

        measurementListView = (ListView) rootView.findViewById(R.id.measurementInstanceListView);
        myListRowAdapterMeasurement = new ListRowAdapterMeasurement(getActivity(), list, sharedPrefActive.getBoolean(getString(R.string.GoalIsActive),false));
        measurementListView.setAdapter(myListRowAdapterMeasurement);
        measurementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doneButton.setText("Update");
                undoButton.setText("Delete");
                lastPosition = position;
                isItemSelectedBoolean = true;
            }
        });

        //Checks to see if its empty, if not then populate
        populateList();

        doneButton = (Button) rootView.findViewById(R.id.logMeasurementDone);
        doneButton.setText("Log Measurement");
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneButtonPressed();
            }
        });

        undoButton = (Button) rootView.findViewById(R.id.logMeasurementUndo);
        undoButton.setText("Undo");
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoButtonPressed();
            }
        });

        incrementButton = (Button) rootView.findViewById(R.id.incrementButton);
        incrementButton.setText("+");
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementButtonPressed();
            }
        });

        decrementButton = (Button) rootView.findViewById(R.id.decrementButton);
        decrementButton.setText("-");
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementButtonPressed();
            }
        });

        goalTextView = (TextView) rootView.findViewById(R.id.MeasurementGoalTextView);

        //if this is was recreated because we added a goal.
        //update goals just fills the textview

        //should include setting to hide textView

        //SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.GoalViewKey), Context.MODE_PRIVATE);

        if(sharedPref.getBoolean(getString(R.string.GoalViewKey),false) && sharedPrefActive.getBoolean(getString(R.string.GoalIsActive),false) ){

            goalTextView.setVisibility(View.VISIBLE);
            updateGoals();

        }else{
            goalTextView.setVisibility(View.GONE);

        }

        editText = (EditText) rootView.findViewById(R.id.MeasurementEditText);
        editText.setMaxLines(1);

        if(list.size() == 1) {
            editText.setText("0");
        }else{
            editText.setText( Double.toString(measurementObjectsArray.get(1).returnMeasurementValue()) );
        }

        return rootView;
    };

    public void doneButtonPressed() {

        SharedPreferences sharedPrefActive = getActivity().getSharedPreferences(getString(R.string.GoalIsActive), Context.MODE_PRIVATE);

        if(isItemSelectedBoolean){

            //If it tries to change the first row, or goal.
            if(lastPosition == 0){

                isItemSelectedBoolean = false;
                doneButton.setText("Log Measurement");
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Can't edit that one!", Toast.LENGTH_SHORT);
                toast.show();

            }else{

                //Update
                hashMapbackup = list.get(lastPosition);
                measurementObjectBackup = measurementObjectsArray.get(lastPosition);

                //Database Chunk -- these are the OLD values
                final Calendar myCal = measurementObjectBackup.returnTimeStamp();
                final double finalMeasurement = measurementObjectBackup.returnMeasurementValue();
                final boolean finalBoolean = measurementObjectBackup.isGoalStart();

                //This object is always going to hold the old value
                MeasurementObject tempMObj = new MeasurementObject();
                tempMObj.setMeasurmentValue(finalMeasurement);
                tempMObj.setTimeStamp(myCal);
                tempMObj.setIsGoalStart(finalBoolean);

                //This is the new updated value to replace the old one
                MeasurementObject myMeasurementObject = new MeasurementObject();
                myMeasurementObject.setMeasurmentValue(Double.parseDouble(editText.getText().toString()));

                //time and isGoalStart doesn't change
                myMeasurementObject.setTimeStamp(myCal);
                myMeasurementObject.setIsGoalStart(finalBoolean);
                measurementObjectsArray.set(lastPosition, myMeasurementObject);

                HashMap temp = new HashMap();
                temp.put(FIRST_COLUMN, myMeasurementObject.returnMonth() + "/" + myMeasurementObject.returnDay());
                temp.put(SECOND_COLUMN, myMeasurementObject.returnMeasurementValue());


            if(sharedPrefActive.getBoolean(getString(R.string.GoalIsActive),false)){

                if(myMeasurementObject.isGoalStart()){
                    temp.put(THIRD_COLUMN, GOAL_START);
                    baseGoalMeasurement = myMeasurementObject;

                }else {

                    double currentRateHere = calculateRate(myMeasurementObject, baseGoalMeasurement);

                    if (currentRateHere == -1) {
                        //Too Soon
                        temp.put(THIRD_COLUMN, "Too Soon");
                    } else {
                        temp.put(THIRD_COLUMN, currentRateHere);
                    }
                }

            }else{
                //No Goal
                temp.put(THIRD_COLUMN, "");
            }


            list.set(lastPosition, temp);

                updateDatabase(myMeasurementObject, tempMObj, null);

                isItemSelectedBoolean = false;
                doneButton.setText("Log Measurement");
                undoButton.setText("Undo Update");
                myListRowAdapterMeasurement.notifyDataSetChanged();
                lastActionEdit();

                stateVar = 9;

            }

        }else{

            if(!editText.getText().toString().equals("")) {

                //ADD

                Calendar timeNow = Calendar.getInstance();
                MeasurementObject myMeasurementObject = new MeasurementObject();
                myMeasurementObject.setMeasurmentValue(Double.parseDouble(editText.getText().toString()));
                myMeasurementObject.setTimeStamp(timeNow);
                measurementObjectsArray.add(1, myMeasurementObject);

                HashMap temp = new HashMap();
                temp.put(FIRST_COLUMN, myMeasurementObject.returnMonth() + "/" + myMeasurementObject.returnDay());
                temp.put(SECOND_COLUMN, myMeasurementObject.returnMeasurementValue());

                //thirdCol
                if(thirdColumnBoolean){

                    double timeDiffVar = (baseGoalMeasurement.returnTimeStamp().getTimeInMillis() - myMeasurementObject.returnTimeStamp().getTimeInMillis()) / 86400000;
                    double currentRate = ((myGoalValue - initialValue)/timeDiffVar);

                    //TODO - not working!!!
                    if(timeDiffVar < .000694){
                        //Wait at least 1 min, 1 timeDiffVar = 1 day. so 1/(24*60) is 1 minute
                        temp.put(THIRD_COLUMN,"Too Soon");
                    }else if(currentRate < 0){
                        //absolute value
                        currentRate = -1*currentRate;
                        temp.put(THIRD_COLUMN,currentRate);
                    }else {
                        temp.put(THIRD_COLUMN, currentRate);
                    }
                }

                list.add(1, temp);

                addDatabase(myMeasurementObject);
                //printDatabase();

                isItemSelectedBoolean = false;
                doneButton.setText("Log Measurement");
                undoButton.setText("Undo Add");
                myListRowAdapterMeasurement.notifyDataSetChanged();
                lastActionAdd();
                stateVar = 3;
                //lastPosition = list.indexOf(temp);
                lastPosition = 1;
            }

        }

       //Update Goal
        updateGoals();

    }

    public void updateDatabase(MeasurementObject newObj, MeasurementObject oldObj, String deletedString){

        ContentValues values = new ContentValues();
        values.put(MeasurementTable.MEASUREMENT, measurementName);
        values.put(MeasurementTable.MEASUREMENT_VALUE, newObj.returnMeasurementValue() );

        if(deletedString == null) {
            //this is actually updating a value
            values.put(MeasurementTable.CALENDAR, newObj.returnTimeStamp().getTime().toString());

        }else{
            values.put(MeasurementTable.CALENDAR, deletedString);
        }

        String goalStart;
        if(oldObj.isGoalStart()) {
            values.put(MeasurementTable.GOAL_START, GOAL_START);
            goalStart = GOAL_START;

        }else{
            values.put(MeasurementTable.GOAL_START, "0");
            goalStart = "0";
        }

        String selection =
                MeasurementTable.MEASUREMENT + " =? AND "
                        + MeasurementTable.MEASUREMENT_VALUE + " =? AND "
                        + MeasurementTable.CALENDAR + " =? AND "
                        + MeasurementTable.GOAL_START + " =?"
                ;


        String[] selectionargs = {
                measurementName,
                Double.toString(oldObj.returnMeasurementValue()),
                oldObj.returnTimeStamp().getTime().toString(),
                goalStart
        };

        MeasurementTableDB.update(
                MeasurementTable.TABLE_NAME,
                values,
                selection,
                selectionargs
        );

        //printDatabase();

    }

    public void removeDatabase(MeasurementObject myMeasurementObject, Boolean undo){

        final Calendar myCal = myMeasurementObject.returnTimeStamp();
        final double finalValue = myMeasurementObject.returnMeasurementValue();
        final Boolean finalBool = myMeasurementObject.isGoalStart();

        MeasurementObject newObj = new MeasurementObject();
        newObj.setMeasurmentValue(finalValue);
        newObj.setTimeStamp(myCal);
        newObj.setIsGoalStart(finalBool);

        if(undo){
            //re-add, by updating the deleted one with the correct calendar date
            updateDatabase(newObj, myMeasurementObject, myMeasurementObject.returnTimeStamp().getTime().toString());

        }else{

            //remove
            updateDatabase(newObj, myMeasurementObject, "0");
        }
    }

    public void addDatabase(MeasurementObject myMeasurementObject){

        ContentValues values = new ContentValues();
        values.put(MeasurementTable.MEASUREMENT, measurementName);
        values.put(MeasurementTable.MEASUREMENT_VALUE, myMeasurementObject.returnMeasurementValue());
        values.put(MeasurementTable.CALENDAR, myMeasurementObject.returnTimeStamp().getTime().toString());

        if(myMeasurementObject.isGoalStart()) {
            values.put(MeasurementTable.GOAL_START, GOAL_START);
        }else{
            values.put(MeasurementTable.GOAL_START, "0");
        }

        MeasurementTableDB.insert(MeasurementTable.TABLE_NAME, null, values);

    }

    public void restoreDatabaseItem(MeasurementObject tempObject){

        ContentValues values = new ContentValues();
        values.put(MeasurementTable.MEASUREMENT, measurementName);
        values.put(MeasurementTable.MEASUREMENT_VALUE, tempObject.returnMeasurementValue() );
        values.put(MeasurementTable.CALENDAR, tempObject.returnTimeStamp().getTime().toString());

        String goalStart;
        if(tempObject.isGoalStart()) {
            values.put(MeasurementTable.GOAL_START, GOAL_START);
            goalStart = GOAL_START;
        }else{
            values.put(MeasurementTable.GOAL_START, "0");
            goalStart = "0";
        }

        String selection =
                MeasurementTable.MEASUREMENT + " =? AND "
                        + MeasurementTable.MEASUREMENT_VALUE + " =? AND "
                        + MeasurementTable.CALENDAR + " =? AND "
                        + MeasurementTable.GOAL_START + " =?"
                ;


        String[] selectionargs = {
                measurementName,
                Double.toString(tempObject.returnMeasurementValue()),
                "0",
                goalStart
        };

        MeasurementTableDB.update(
                MeasurementTable.TABLE_NAME,
                values,
                selection,
                selectionargs
        );

        //printDatabase();

    }

    public void undoButtonPressed(){

        if(isItemSelectedBoolean){

            //Delete

            if(lastPosition == 0){

                isItemSelectedBoolean = false;
                undoButton.setText("Undo");
                doneButton.setText("Log Instances");

            }else {


                if(thirdColumnBoolean && list.get(lastPosition).get(THIRD_COLUMN).toString().equals(GOAL_START)){
                    //if removing Goal
                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                    adb.setTitle("Remove Goal?");
                    adb.setMessage("This will remove the goal");
                    final int positionToRemove = lastPosition;
                    adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            removeDatabase(measurementObjectsArray.get(lastPosition),false);
                            list.remove(positionToRemove);
                            measurementObjectsArray.remove(positionToRemove);
                            myListRowAdapterMeasurement.notifyDataSetChanged();
                            remakeFrag(false);

                        }

                    });

                    adb.setNegativeButton("Cancel", null);
                    adb.show();

                }else {

                    hashMapbackup = list.get(lastPosition);

                    MeasurementObject MeasurementObjectBackupTEMP = new MeasurementObject();
                    final Calendar finalCal = measurementObjectsArray.get(lastPosition).returnTimeStamp();
                    final double finalMeasureValue = measurementObjectsArray.get(lastPosition).returnMeasurementValue();
                    final boolean finalBool = measurementObjectsArray.get(lastPosition).isGoalStart();
                    MeasurementObjectBackupTEMP.setIsGoalStart(finalBool);
                    MeasurementObjectBackupTEMP.setMeasurmentValue(finalMeasureValue);
                    MeasurementObjectBackupTEMP.setTimeStamp(finalCal);
                    measurementObjectBackup = MeasurementObjectBackupTEMP;

                    removeDatabase(measurementObjectsArray.get(lastPosition),false);

                    list.remove(lastPosition);
                    measurementObjectsArray.remove(lastPosition);
                    myListRowAdapterMeasurement.notifyDataSetChanged();

                    isItemSelectedBoolean = false;
                    undoButton.setText("Undo Delete");
                    doneButton.setText("Log Instances");
                    stateVar = 1;
                    lastActionDelete();
                }

            }

        }else{

            //Undo or Redo
            if(lastActionAddBoolean){
                //Delete
                undoButton.setText("Redo Delete");
                lastActionDelete();

                MeasurementObject MeasurementObjectBackupTEMP = new MeasurementObject();
                final Calendar finalCal = measurementObjectsArray.get(lastPosition).returnTimeStamp();
                final double finalMeasureValue = measurementObjectsArray.get(lastPosition).returnMeasurementValue();
                final boolean finalBool = measurementObjectsArray.get(lastPosition).isGoalStart();
                MeasurementObjectBackupTEMP.setIsGoalStart(finalBool);
                MeasurementObjectBackupTEMP.setMeasurmentValue(finalMeasureValue);
                MeasurementObjectBackupTEMP.setTimeStamp(finalCal);
                measurementObjectBackup = MeasurementObjectBackupTEMP;

                removeDatabase(measurementObjectsArray.get(lastPosition), false);
                hashMapbackup = list.get(lastPosition);
                list.remove(lastPosition);
                measurementObjectsArray.remove(lastPosition);
                myListRowAdapterMeasurement.notifyDataSetChanged();

                if(stateVar == 2){
                    stateVar = 1;
                    undoButton.setText("Undo Delete");
                    doneButton.setText("Log Instances");
                }else if(stateVar == 3){
                    stateVar = 4;
                    undoButton.setText("Redo Add");
                    doneButton.setText("Log Instances");

                }


            }else if(lastActionDeleteBoolean){
                //Undo delete


                restoreDatabaseItem(measurementObjectBackup);

                measurementObjectsArray.add(lastPosition, measurementObjectBackup);
                list.add(lastPosition, hashMapbackup);
                myListRowAdapterMeasurement.notifyDataSetChanged();
                lastActionAdd();

                if(stateVar == 1){
                    stateVar = 2;
                    undoButton.setText("Redo Delete");
                    doneButton.setText("Log Instances");
                }else if(stateVar == 4){
                    stateVar = 3;
                    undoButton.setText("Undo Add");
                    doneButton.setText("Log Instances");
                }

            }else if(lastActionEditBoolean){

                MeasurementObject MeasurementObjectBackupTemp = measurementObjectsArray.get(lastPosition);
                HashMap hashMapBackupTemp = list.get(lastPosition);

                // STORING OLD OBJECT
                final Calendar finalCalendar = MeasurementObjectBackupTemp.returnTimeStamp();
                final double finalValue = MeasurementObjectBackupTemp.returnMeasurementValue();
                final boolean isGoalStart = MeasurementObjectBackupTemp.isGoalStart();

                MeasurementObject oldMeasurementObj = new MeasurementObject();
                oldMeasurementObj.setIsGoalStart(isGoalStart);
                oldMeasurementObj.setTimeStamp(finalCalendar);
                oldMeasurementObj.setMeasurmentValue(finalValue);

                //Restore Value as intended
                measurementObjectsArray.set(lastPosition, measurementObjectBackup);
                list.set(lastPosition, hashMapbackup);
                myListRowAdapterMeasurement.notifyDataSetChanged();

                updateDatabase(measurementObjectBackup, oldMeasurementObj, null);

                //Set the value we replaced into the temps as backups
                measurementObjectBackup = MeasurementObjectBackupTemp;
                hashMapbackup= hashMapBackupTemp;
                lastActionEdit();

                if(stateVar == 9) {
                    undoButton.setText("Undo Update");
                    doneButton.setText("Log Instances");
                    stateVar = 10;
                }else if(stateVar == 10){
                    undoButton.setText("Redo Update");
                    doneButton.setText("Log Instances");
                    stateVar = 9;
                }


            }

        }

        updateGoals();

    }

    public void printDatabase(){
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

            Log.d("TABLE ITEM", tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(MeasurementTable.GOAL_START))
            );

        }

        while (tempCur.moveToNext()) {

            Log.d("TABLE ITEM", tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR))
                            + " - " + tempCur.getString(tempCur.getColumnIndex(MeasurementTable.GOAL_START))
            );

        }
    }

    public void incrementButtonPressed(){

        try{
            editText.setText(Double.toString(Double.parseDouble(editText.getText().toString()) + 5));
        }catch(Exception e){
            editText.setText("5");
        }
    }

    public void decrementButtonPressed(){
        try{
            editText.setText(Double.toString(Double.parseDouble(editText.getText().toString()) - 5));
        }catch(Exception e){
            editText.setText("0");
        }
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

    public void updateGoals(){

        SharedPreferences sharedPrefActive = getActivity().getSharedPreferences(getString(R.string.GoalIsActive), Context.MODE_PRIVATE);

        SharedPreferences sharedPrefTimeValues = getActivity().getSharedPreferences(getString(R.string.TimeVarSet), Context.MODE_PRIVATE);

        //Update Goal
            if(sharedPrefActive.getBoolean(getString(R.string.GoalIsActive),false)) {

                if(sharedPrefTimeValues.getLong(getString(R.string.TimeVar1), 0) > 0){
                    timeDiff = ( sharedPrefTimeValues.getLong(getString(R.string.TimeVar1), 0) - Calendar.getInstance().getTimeInMillis()) / 86400000;
                }else {
                    timeDiff = (baseGoalMeasurement.returnTimeStamp().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 86400000;
                }

                if(sharedPrefTimeValues.getLong(getString(R.string.TimeVar2), 0) > 0){
                    goalDiffGainNOCHANGE = (double) sharedPrefTimeValues.getLong(getString(R.string.TimeVar2), 0);
                }else{
                    goalDiffGainNOCHANGE = myGoalValue - initialValue;
                }

                goalDiffLossNOCHANGE = -1*goalDiffGainNOCHANGE;

                if(sharedPrefTimeValues.getLong(getString(R.string.TimeVar3), 0) > 0) {
                    goalDiffGain = (double) sharedPrefTimeValues.getLong(getString(R.string.TimeVar3), 0) - baseGoalMeasurement.returnMeasurementValue();
                }else{
                    goalDiffGain = measurementObjectsArray.get(1).returnMeasurementValue() - baseGoalMeasurement.returnMeasurementValue();
                }

                goalDiffLoss = -1*goalDiffGain;

                if(sharedPrefTimeValues.getLong(getString(R.string.TimeVar3), 0) > 0){
                    myGoalCalendar = (int) sharedPrefTimeValues.getLong(getString(R.string.TimeVar3), 0);
                }

                SharedPreferences.Editor editor = sharedPrefTimeValues.edit();
                editor.putLong(getString(R.string.TimeVar1), baseGoalMeasurement.returnTimeStamp().getTimeInMillis());
                editor.putLong(getString(R.string.TimeVar2), (long) goalDiffGainNOCHANGE);
                editor.putLong(getString(R.string.TimeVar3), (long) measurementObjectsArray.get(1).returnMeasurementValue());
                editor.putLong(getString(R.string.TimeVar4), (long) myGoalCalendar);
                editor.apply();

                if (goalIsToLoseWeight) {

                    if(myGoalCalendar == 10000){
                     //if I didn't set a goal in days

                        goalTextView.setText("Lose " + goalDiffLossNOCHANGE);

                    }else {

                        goalTextView.setText(goalDiffLossNOCHANGE + " in " + myGoalCalendar + " days at a rate of " + new DecimalFormat("##.##").format(goalDiffLossNOCHANGE/myGoalCalendar) + " a day. \n You've lost " +
                                goalDiffLoss + " in " + timeDiff + " days");
                    }

                } else {

                    if(myGoalCalendar == 10000){
                        //if I didn't set a goal in days

                        goalTextView.setText("Gain " + goalDiffGainNOCHANGE);

                    }else {

                        goalTextView.setText(goalDiffGainNOCHANGE + " in " + myGoalCalendar + " days at a rate of " + new DecimalFormat("##.##").format(goalDiffGainNOCHANGE/myGoalCalendar) + " a day. \n You've gained " +
                                goalDiffGain + " in " + timeDiff + " days");
                    }

                }

            }
        }

}
