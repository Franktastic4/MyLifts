package com.example.franktastic4.mylifts.MeasurementPackage;

import android.provider.BaseColumns;

/**
 * Created by Franktastic4 on 7/13/15.
 */
public class MeasurementTableReaderContract {

    public MeasurementTableReaderContract(){

    }
    /* Inner class that defines the table contents */
    public static abstract class MeasurementTable implements BaseColumns {
        //BaseColumns give _ID
        public static final String TABLE_NAME = "MeasurementTable";
        public static final String MEASUREMENT = "Measurement";
        public static final String MEASUREMENT_VALUE = "MeasurementValue";
        public static final String CALENDAR = "Calender";

        //Is Goal
        public static final String GOAL_START = "GoalStart";
}

}
