package com.example.franktastic4.mylifts.JournalPackage;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.franktastic4.mylifts.JournalPackage.JournalTableReaderContract.JournalTable;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableDbHelper;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableReaderContract.MeasurementTable;
import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableDbHelper;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableReaderContract.WorkoutTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Franktastic4 on 7/8/15.
 */
public class JournalFragment extends android.support.v4.app.Fragment {

    String dateString;
    boolean isFromCalendarTrueBool = false;
    Calendar myCalendar = Calendar.getInstance();

    MeasurementTableDbHelper mMeasurementDbHelper;
    SQLiteDatabase MeasurementTableDBReadable;

    WorkoutTableDbHelper mWorkoutTableDbHelper;
    SQLiteDatabase WorkoutTableDBReadable;

    JournalTableDbHelper mJournalTableDbHelper;
    SQLiteDatabase JournalTableDBReadable;

    boolean hideEmptyBoolean = false;

    long firstWorkoutDateInMillis;

    private Vibrator myVib;

    public JournalFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myVib = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        //Initiate Databases
        mMeasurementDbHelper = new MeasurementTableDbHelper(getActivity().getApplicationContext());
        MeasurementTableDBReadable = mMeasurementDbHelper.getReadableDatabase();

        mWorkoutTableDbHelper = new WorkoutTableDbHelper(getActivity().getApplicationContext());
        WorkoutTableDBReadable = mWorkoutTableDbHelper.getReadableDatabase();

        mJournalTableDbHelper = new JournalTableDbHelper(getActivity().getApplicationContext());
        JournalTableDBReadable = mJournalTableDbHelper.getReadableDatabase();

        //What if there aren't any workouts yet?
        firstWorkoutDateInMillis = findFirstWorkoutDate();
    }

    @Override
    public void onResume(){
        super.onResume();
            ((JournalActivity) getActivity()).setActionBarTitle("Journal");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_journal, container, false);

        //Everything is set to the Calendar's date
        //Set when recreating fragment, defaults to today

        //If Buttons are pressed, remake the fragment with a new date
        //use setCalendar to set myCalendar
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.HideEmptyJournalEntry), Context.MODE_PRIVATE);

        hideEmptyBoolean = sharedPref.getBoolean(getString(R.string.HideEmptyJournalEntry),false);

        rootView.setOnTouchListener(new OnSwipeTouchListener(getActivity().getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                Date tempDate = findDate(false);
                if(hideEmptyBoolean) {

                    if (tempDate != null) {
                        remakeFragment(tempDate);
                    } else {
                        fillDateTextView(rootView);
                        fillJournalEntryTextView(rootView);
                        fillWorkoutEntryTextView(rootView);
                        fillMeasurementEntryTextView(rootView);
                    }

                }else{
                    remakeFragment(tempDate);
                }

            }

            public void onSwipeRight() {
                Date tempDate = findDate(true);
                if(hideEmptyBoolean) {

                    if (tempDate != null) {
                        remakeFragment(tempDate);
                    } else {
                        fillDateTextView(rootView);
                        fillJournalEntryTextView(rootView);
                        fillWorkoutEntryTextView(rootView);
                        fillMeasurementEntryTextView(rootView);
                    }

                }else{
                    remakeFragment(tempDate);
                }
            }

        });

        //Buttons setup
        Button leftButton = (Button) rootView.findViewById(R.id.leftJournalButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(150);
                Date tempDate = findDate(true);
                if(hideEmptyBoolean) {

                    if (tempDate != null) {
                        remakeFragment(tempDate);
                    } else {
                        fillDateTextView(rootView);
                        fillJournalEntryTextView(rootView);
                        fillWorkoutEntryTextView(rootView);
                        fillMeasurementEntryTextView(rootView);
                    }

                }else{
                    remakeFragment(tempDate);
                }


            }
        });

        Button rightButton = (Button) rootView.findViewById(R.id.rightJournalButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myVib.vibrate(150);
                Date tempDate = findDate(false);
                if(hideEmptyBoolean) {

                    if (tempDate != null) {
                        remakeFragment(tempDate);
                    } else {
                        fillDateTextView(rootView);
                        fillJournalEntryTextView(rootView);
                        fillWorkoutEntryTextView(rootView);
                        fillMeasurementEntryTextView(rootView);
                    }

                }else{
                    remakeFragment(tempDate);
                }
            }
        });

        //View Setups, they find stuff based on date we set
        fillDateTextView(rootView);
        fillJournalEntryTextView(rootView);
        fillWorkoutEntryTextView(rootView);
        fillMeasurementEntryTextView(rootView);

        return rootView;
    }

    public Date findDate(Boolean direction){

        //returns a calender, used a parameter by remakeFrag

        //true means previous date, false means next

        if(direction) {

            if(hideEmptyBoolean) {

                while (myCalendar.getTimeInMillis() >= firstWorkoutDateInMillis) {

                    // looks back one day, null if nothing found
                    Date lastWorkoutCal = findPrevWorkout(direction);

                    // looks back one day, null if nothing found
                    Date lastMeasurementCal = findPrevMeasurement(direction);

                    // looks back one day, null if nothing found
                    Date lastJournalCal = findPrevJournalEntry(direction);

                    if (hideEmptyBoolean) {

                        //if nothing yesterday, go back another day
                        if (lastWorkoutCal == null && lastMeasurementCal == null && lastJournalCal == null) {
                            myCalendar.add(Calendar.DAY_OF_YEAR, -1);
                        } else {
                            myCalendar.add(Calendar.DAY_OF_YEAR, -1);
                            return myCalendar.getTime();
                        }

                    } else {

                        //To only go back one day:
                        myCalendar.add(Calendar.DAY_OF_YEAR, -1);
                        return myCalendar.getTime();

                    }

                }
                Log.d("ERROR", "Can't go any further back!");

            }else{

                myCalendar.add(Calendar.DAY_OF_YEAR, -1);
                return myCalendar.getTime();

            }

        }else {

            if (hideEmptyBoolean) {

                if (myCalendar.get(Calendar.YEAR) <= Calendar.getInstance().get(Calendar.YEAR)) {

                    while (myCalendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {

                        // looks back one day, null if nothing found
                        Date lastWorkoutCal = findPrevWorkout(direction);

                        // looks back one day, null if nothing found
                        Date lastMeasurementCal = findPrevMeasurement(direction);

                        // looks back one day, null if nothing found
                        Date lastJournalCal = findPrevJournalEntry(direction);

                        //if nothing yesterday, repeat until something shows up or last Workout Date
                        if (lastWorkoutCal == null && lastMeasurementCal == null && lastJournalCal == null) {
                            myCalendar.add(Calendar.DAY_OF_YEAR, 1);
                        } else {
                            myCalendar.add(Calendar.DAY_OF_YEAR, 1);
                            return myCalendar.getTime();
                        }

                    }
                }

                Log.d("ERROR", "Can't go any further forward!");

            }else{

                myCalendar.add(Calendar.DAY_OF_YEAR, 1);
                return myCalendar.getTime();

            }

        }

        return null;
    }

    public long findFirstWorkoutDate(){

        String[] myProjection = {
                WorkoutTable.DATE,
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection =
                WorkoutTable.DATE + "!=? AND " +  WorkoutTable.DATE + "!=? AND " + WorkoutTable.DATE + "!=?";

        String[] SelectionArgs = {"0","1","2"};

        Cursor tempCur = WorkoutTableDBReadable.query(
                WorkoutTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

        //find the time before mine

        tempCur.moveToFirst();

        //if no first workout
        if(!tempCur.moveToFirst()){
            return 0;
        }

        Calendar tempCalendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            tempCalendar.setTime(sdf.parse(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE))));
        }catch (Exception e){
            e.printStackTrace();
        }

        return tempCalendar.getTimeInMillis();

    }

    public Date findPrevWorkout(Boolean directionPrev){

        String[] myProjection = {
                WorkoutTable.DATE
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection =
                WorkoutTable.DATE + "!=? AND " +  WorkoutTable.DATE + "!=? AND " + WorkoutTable.DATE + "!=?";

        String[] SelectionArgs = {"0","1","2"};

        Cursor tempCur = WorkoutTableDBReadable.query(
                WorkoutTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

        //find the time before mine

        tempCur.moveToFirst(); // was this originally intended to skip the header
        while(tempCur.moveToNext()){

            //Holds the value of a Workout from the database
            Calendar tempCalendar = Calendar.getInstance();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                tempCalendar.setTime(sdf.parse(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE))));
            }catch (Exception e){
                e.printStackTrace();
            }

            if(directionPrev) {

                if ((myCalendar.get(Calendar.DAY_OF_YEAR) - 1) == tempCalendar.get(Calendar.DAY_OF_YEAR)) {
                    return tempCalendar.getTime();
                }

            }else{

                if ((myCalendar.get(Calendar.DAY_OF_YEAR) + 1) == tempCalendar.get(Calendar.DAY_OF_YEAR)) {
                    return tempCalendar.getTime();
                }

            }

        }

        return null;
    }

    public Date findPrevMeasurement(Boolean directionPrev){
        String[] myProjection = {
                MeasurementTable.MEASUREMENT,
                MeasurementTable.MEASUREMENT_VALUE,
                MeasurementTable.CALENDAR
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection =
                MeasurementTable.CALENDAR + "!=? AND " +  MeasurementTable.CALENDAR + "!=? AND " + MeasurementTable.CALENDAR + "!=?";

        String[] SelectionArgs = {"0","1","2"};

        Cursor tempCur = MeasurementTableDBReadable.query(
                MeasurementTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

        //find the time before mine

        tempCur.moveToFirst();
        while(tempCur.moveToNext()){

            //Holds the value of a Workout from the database
            Calendar tempCalendar = Calendar.getInstance();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                tempCalendar.setTime(sdf.parse(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR))));
            }catch (Exception e){
                e.printStackTrace();
            }

            if(directionPrev) {

                if ((myCalendar.get(Calendar.DAY_OF_YEAR) - 1) == tempCalendar.get(Calendar.DAY_OF_YEAR)) {
                    return tempCalendar.getTime();
                }

            }else{

                if ((myCalendar.get(Calendar.DAY_OF_YEAR) + 1) == tempCalendar.get(Calendar.DAY_OF_YEAR)) {
                    return tempCalendar.getTime();
                }

            }

        }

        return null;
    }

    public Date findPrevJournalEntry(Boolean directionPrev){

        String[] myProjection = {
                JournalTable.ENTRY,
                JournalTable.CALENDAR,
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection =
                JournalTable.CALENDAR + "!=?";

        String[] SelectionArgs = {"0"};

        Cursor tempCur = JournalTableDBReadable.query(
                JournalTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

        //find the time before mine

        tempCur.moveToFirst();
        while(tempCur.moveToNext()){

            //Holds the value of a Workout from the database
            Calendar tempCalendar = Calendar.getInstance();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                tempCalendar.setTime(sdf.parse(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR))));
            }catch (Exception e){
                e.printStackTrace();
            }

            if(directionPrev) {

                if ((myCalendar.get(Calendar.DAY_OF_YEAR) - 1) == tempCalendar.get(Calendar.DAY_OF_YEAR)) {
                    return tempCalendar.getTime();
                }

            }else{

                if ((myCalendar.get(Calendar.DAY_OF_YEAR) + 1) == tempCalendar.get(Calendar.DAY_OF_YEAR)) {
                    return tempCalendar.getTime();
                }

            }

        }

        return null;

    }

    public void remakeFragment(Date newCal){

        //Called by left/right buttons, parameter newCal is given by findDate
        MeasurementTableDBReadable.close();
        WorkoutTableDBReadable.close();
        JournalTableDBReadable.close();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        JournalFragment myJournalFragment = new JournalFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container_journal, myJournalFragment, "journal_fragment_tag")
                .commit();

        if(newCal == null){

            newCal = myCalendar.getTime();

        }


        Calendar tempCal = Calendar.getInstance();
        tempCal.setTime(newCal);
        myJournalFragment.setCalendar(tempCal);
        myJournalFragment.setFromCalenderTrue(isFromCalendarTrueBool);

    }

    public void fillDateTextView(View rootView){

        TextView dateTextView = (TextView) rootView.findViewById(R.id.DateTextView);
        dateString = myCalendar.get(Calendar.MONTH) + "/" + myCalendar.get(Calendar.DAY_OF_MONTH);
        dateTextView.setText("Entry for : " + dateString);
    }

    public void fillJournalEntryTextView(View rootView) {

        TextView JournalEntryTextView = (TextView) rootView.findViewById(R.id.JournalEntryTextView);

        String[] myProjection = {
                JournalTable.ENTRY,
                JournalTable.CALENDAR,
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection =
                JournalTable.CALENDAR + "!=?";

        String[] SelectionArgs = {"0"};

        Cursor tempCur = JournalTableDBReadable.query(
                JournalTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

        //find the time before mine

        //printCalendarDatabase();

        String tempJournalEntry = "";
        tempCur.moveToFirst();
        while (tempCur.moveToNext()) {

            Calendar tempCalendar = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                tempCalendar.setTime(sdf.parse(tempCur.getString(tempCur.getColumnIndex(JournalTable.CALENDAR))));
            }catch (Exception e){
                e.printStackTrace();
            }

            if(tempCalendar.get(Calendar.DAY_OF_YEAR) == myCalendar.get(Calendar.DAY_OF_YEAR)) {
                tempJournalEntry = tempJournalEntry + " " + tempCur.getString(tempCur.getColumnIndex(JournalTable.ENTRY));
            }

        }

        if (tempJournalEntry.equals("")) {
            JournalEntryTextView.setText("No journal entry on this date");
        } else {
            JournalEntryTextView.setText(tempJournalEntry);
        }
    }

    public void printCalendarDatabase(){

        String[] myProjection = {
                JournalTable.ENTRY,
                JournalTable.CALENDAR,
        };

        Cursor tempCur = JournalTableDBReadable.query(
                JournalTable.TABLE_NAME,
                myProjection,
                null,
                null,
                null,
                null,
                null);

        tempCur.moveToFirst();
        while(tempCur.moveToNext()){

            Log.d("Print Database",tempCur.getString(tempCur.getColumnIndex(JournalTable.ENTRY)) + " on Date: " + tempCur.getString(tempCur.getColumnIndex(JournalTable.CALENDAR)));

        }


    }

    public void fillWorkoutEntryTextView(View rootView){

        TextView WorkoutEntryTextView = (TextView) rootView.findViewById(R.id.WorkoutEntryTextView);

        String[] myProjection = {
                WorkoutTable.WORKOUT_NAME,
                WorkoutTable.EXERCISE_NAME,
                WorkoutTable.DATE,
                WorkoutTable.SET,
                WorkoutTable.REPS,
                WorkoutTable.WEIGHT
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection =
                WorkoutTable.DATE + "!=? AND " +  WorkoutTable.DATE + "!=? AND " + WorkoutTable.DATE + "!=?";

        String[] SelectionArgs = {"0","1","2"};

        Cursor tempCur = WorkoutTableDBReadable.query(
                WorkoutTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

        //find the time before mine

        String tempWorkoutEntry = "";
        tempCur.moveToFirst();
        while(tempCur.moveToNext()){

            Calendar tempCalendar = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                tempCalendar.setTime(sdf.parse(tempCur.getString(tempCur.getColumnIndex(WorkoutTable.DATE))));
            }catch (Exception e){
                e.printStackTrace();
            }

            if(tempCalendar.get(Calendar.DAY_OF_YEAR) == myCalendar.get(Calendar.DAY_OF_YEAR)) {

                tempWorkoutEntry = tempWorkoutEntry
                        + "\nWorkout: " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WORKOUT_NAME))
                        + " | Exercise: " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.EXERCISE_NAME))
                        + " | Set: " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.SET))
                        + " | Reps: " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.REPS))
                        + " | Weight: " + tempCur.getString(tempCur.getColumnIndex(WorkoutTable.WEIGHT));

            }

        }

        if(tempWorkoutEntry.equals("")){
            WorkoutEntryTextView.setText("No workout entry on this date");
        }else {
            WorkoutEntryTextView.setText(tempWorkoutEntry);
        }
    }

    public void fillMeasurementEntryTextView(View rootView){

        TextView MeasurementEntryTextView = (TextView) rootView.findViewById(R.id.MeasurementEntryTextView);

        String[] myProjection = {
                MeasurementTable.MEASUREMENT,
                MeasurementTable.MEASUREMENT_VALUE,
                MeasurementTable.CALENDAR
        };

        //need workoutname parameter in the querey otherwise leg exercies go into chest too
        String selection =
                MeasurementTable.CALENDAR + "!=? AND " +  MeasurementTable.CALENDAR + "!=? AND " + MeasurementTable.CALENDAR + "!=?";

        String[] SelectionArgs = {"0","1","2"};

        Cursor tempCur = MeasurementTableDBReadable.query(
                MeasurementTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

        //find the time before mine

        String tempMeasurementEntry = "";
        tempCur.moveToFirst();
        while(tempCur.moveToNext()){

            Calendar tempCalendar = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                tempCalendar.setTime(sdf.parse(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR))));
            }catch (Exception e){
                e.printStackTrace();
            }

            if(tempCalendar.get(Calendar.DAY_OF_YEAR) == myCalendar.get(Calendar.DAY_OF_YEAR)) {

                tempMeasurementEntry = tempMeasurementEntry
                        + "\nMeasurement: " + tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT))
                        + " | Measurement Value: " + tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE));

            }

        }

        if(tempMeasurementEntry.equals("")){
            MeasurementEntryTextView.setText("No measurement entry on this date");
        }else {
            MeasurementEntryTextView.setText(tempMeasurementEntry);
        }
    }

    public void setCalendar(Calendar newCal){
        myCalendar = newCal;
    }

    public void setFromCalenderTrue(boolean newBool){ isFromCalendarTrueBool = newBool; }


}
