package com.example.franktastic4.mylifts.AnalysisPackage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.franktastic4.mylifts.AutoResizeTextView;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableDbHelper;
import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableReaderContract.MeasurementTable;
import com.example.franktastic4.mylifts.R;
import com.example.franktastic4.mylifts.WorkoutListPackage.WorkoutTableDbHelper;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;





/**
 * Created by Franktastic4 on 7/29/15.
 */

public class VisualAnalysisFragment extends android.support.v4.app.Fragment {

    public VisualAnalysisFragment(){}
    String exerciseName;
    String workoutName;
    boolean isWorkoutTrue;

    MeasurementTableDbHelper mMeasurementDbHelper;
    SQLiteDatabase MeasurementTableDBReadable;

    WorkoutTableDbHelper mWorkoutTableDbHelper;
    SQLiteDatabase WorkoutTableDBReadable;

    LineChart myPerformanceChart;
    LineChart myRateOfGrowthLineChart;
    CombinedChart myRepsAndWeightChart;

    ArrayList<Entry> performanceEntry;
    ArrayList<String> xVals;

    ArrayList<Entry> rateOfChangeEntry;
    ArrayList<String> xValsRateOfChange;

    AutoResizeTextView TextViewPerformanceChart;
    AutoResizeTextView TextViewRateOfGrowthLineChart;
    AutoResizeTextView TextViewRepsAndWeight;


    public void onResume() {

        if(isWorkoutTrue) {
            ((AnalysisActivity) getActivity()).setActionBarTitle(exerciseName);
        }else{
            ((AnalysisActivity) getActivity()).setActionBarTitle(exerciseName);
        }

        super.onResume();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_visual_analysis_fragment, container, false);

        performanceEntry = new ArrayList<Entry>();
        xVals = new ArrayList<String>();

        rateOfChangeEntry = new ArrayList<Entry>();
        xValsRateOfChange = new ArrayList<String>();

        TextViewPerformanceChart = (AutoResizeTextView) rootView.findViewById(R.id.TextViewPerformanceChart);
        TextViewRateOfGrowthLineChart = (AutoResizeTextView) rootView.findViewById(R.id.TextViewRateOfGrowthLineChart);
        TextViewRepsAndWeight = (AutoResizeTextView) rootView.findViewById(R.id.TextViewRepsAndWeight);

        //Have user select by Week, month, year, total.
        myPerformanceChart = (LineChart) rootView.findViewById(R.id.WeightLineChart);
        myRateOfGrowthLineChart = (LineChart) rootView.findViewById(R.id.RateOfGrowthLineChart);
        myRepsAndWeightChart = (CombinedChart) rootView.findViewById(R.id.RepsAndWeightChart);

        myPerformanceChart.setNoDataTextDescription("Insufficient Data");
        myRateOfGrowthLineChart.setNoDataTextDescription("Insufficient Data");


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;

        if(!isWorkoutTrue){

            TextViewPerformanceChart.setText("Graph of Measurements");
            TextViewRateOfGrowthLineChart.setText("Rate of Change");

            myRepsAndWeightChart.setVisibility(View.GONE);
            TextViewRepsAndWeight.setVisibility(View.GONE);

            fillMyMeasurementPerformanceChart();
            fillMyRateOfGrowthLineChart();

            myPerformanceChart.setMinimumHeight((int) (1.2 * dpHeight));
            myRateOfGrowthLineChart.setMinimumHeight( (int)( 1.2 * dpHeight) );

        }else{

            TextViewPerformanceChart.setText("Graph of Performance");
            TextViewRateOfGrowthLineChart.setText("Rate of Change");
            TextViewRepsAndWeight.setText("Reps and Weight");

            myRepsAndWeightChart.setNoDataTextDescription("Insufficient Data");
            fillMyWorkoutPerformanceChart();
        }




        return rootView;
    }

    public void setParameters(String WorkoutName, String ExerciseName, boolean isWorkout){
        workoutName = WorkoutName;
        exerciseName = ExerciseName;
        isWorkoutTrue = isWorkout;
    }

    public void fillMyWorkoutPerformanceChart(){

        /*Take average of the day, and compare to average for the exercise.
         To plot average of the day:
         Average

         To plot average of the day of the exercise:
         Average as of date we're on.
         */

        mWorkoutTableDbHelper = new WorkoutTableDbHelper(getActivity().getApplicationContext());
        WorkoutTableDBReadable = mWorkoutTableDbHelper.getReadableDatabase();

        ArrayList<String> AverageSoFar = new ArrayList<String>();
        //LineDataSet setComp1 = new LineDataSet(valsComp1, "Company 1");

    }

    public void fillMyMeasurementPerformanceChart(){
        mMeasurementDbHelper = new MeasurementTableDbHelper(getActivity().getApplicationContext());
        MeasurementTableDBReadable = mMeasurementDbHelper.getReadableDatabase();

        String[] myProjection = {
                MeasurementTable.MEASUREMENT,
                MeasurementTable.MEASUREMENT_VALUE,
                MeasurementTable.CALENDAR
        };

        String selection = MeasurementTable.MEASUREMENT
                + " =? AND "+ MeasurementTable.CALENDAR + " !=? AND "
                + MeasurementTable.CALENDAR + " !=?";

        String[] SelectionArgs = {exerciseName,"1","0"};

        Cursor tempCur = MeasurementTableDBReadable.query(
               MeasurementTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

        int indexOfEntry = 0;
        if(tempCur.moveToFirst()){

            Entry temp = new Entry(
                    Float.parseFloat(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE)))
                    , indexOfEntry++);
            performanceEntry.add(temp);

            String dateString = tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR));
            Calendar cal = Calendar.getInstance();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                cal.setTime(sdf.parse(dateString));
            } catch (java.text.ParseException e) {

            }

            xVals.add(cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH));

        }

        while(tempCur.moveToNext()){

            Entry temp = new Entry(
                    Float.parseFloat(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE)))
                    , indexOfEntry++);
            performanceEntry.add(temp);

            String dateString = tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR));
            Calendar cal = Calendar.getInstance();

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                cal.setTime(sdf.parse(dateString));
            } catch (java.text.ParseException e) {

            }

            xVals.add(cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH));

        }

        LineDataSet dateLineDataSet = new LineDataSet(performanceEntry, "Dates");
        dateLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(dateLineDataSet);

        LineData data = new LineData(xVals, dataSets);
        myPerformanceChart.setData(data);
        myPerformanceChart.invalidate(); // refresh

    }

    public void fillMyRateOfGrowthLineChart(){

        String[] myProjection = {
                MeasurementTable.MEASUREMENT,
                MeasurementTable.MEASUREMENT_VALUE,
                MeasurementTable.CALENDAR
        };

        String selection = MeasurementTable.MEASUREMENT
                + " =? AND "+ MeasurementTable.CALENDAR + " !=? AND "
                + MeasurementTable.CALENDAR + " !=?";

        String[] SelectionArgs = {exerciseName,"1","0"};

        Cursor tempCur = MeasurementTableDBReadable.query(
                MeasurementTable.TABLE_NAME,
                myProjection,
                selection,
                SelectionArgs,
                null,
                null,
                null);

        int indexOfEntry = 0;

        Calendar calPrev = Calendar.getInstance();
        DateTime dateTimePrev = null;
        double prevValue = 0;

        if(tempCur.moveToFirst()){

            // SET TIME
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                calPrev.setTime(sdf.parse(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR))));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            dateTimePrev = new DateTime(calPrev.getTime());

            // SET PLOT
            prevValue = Double.parseDouble(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE)));
            Entry temp = new Entry(
                     0
                    , indexOfEntry++);

            // ADD TO GRAPH
            rateOfChangeEntry.add(temp);
            xValsRateOfChange.add(calPrev.get(Calendar.MONTH) + "/" + calPrev.get(Calendar.DAY_OF_MONTH));

            // PREV information already set

        }

        while(tempCur.moveToNext()) {

            // get time of the current plot
            Calendar cal = Calendar.getInstance();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                cal.setTime(sdf.parse(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.CALENDAR))));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            DateTime dateTime = new DateTime(cal.getTime());

            //Calculate difference in days
            int changeInTime = Days.daysBetween(dateTimePrev, dateTime).getDays();

            // If the difference is less than a day, ignore this entry.
            // The prev information shouldnt change
            if (changeInTime >= 1) {

                // This measurement - the last VALUE NOT PLOT. prevValue should be the last measurement taken from database.
                // Set Plot

                Log.d("VALUE","CurrValue " + Double.parseDouble(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE))));
                Log.d("VALUE","PrevValue: " + prevValue);
                Log.d("Time","Change in time: " + changeInTime);

                Double changeInMeasurement = Double.parseDouble(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE))) - prevValue;

                // Add Entry to graph
                Entry temp = new Entry((long) (changeInMeasurement / changeInTime), indexOfEntry++);
                rateOfChangeEntry.add(temp);
                xValsRateOfChange.add(cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH));

                // Setting prevValue to the last VALUE of the last plot
                // Update the date of the last plot if we plotted
                prevValue = Double.parseDouble(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE)));
                dateTimePrev = dateTime;
                
            }else{

                //prevValue = Double.parseDouble(tempCur.getString(tempCur.getColumnIndex(MeasurementTable.MEASUREMENT_VALUE)));
                //dateTimePrev = dateTime;

            }


        }

        LineDataSet dateLineDataSet = new LineDataSet(rateOfChangeEntry, "Dates");
        dateLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(dateLineDataSet);

        LineData data = new LineData(xValsRateOfChange, dataSets);
        myRateOfGrowthLineChart.setData(data);
        myRateOfGrowthLineChart.invalidate(); // refresh

    }


}
