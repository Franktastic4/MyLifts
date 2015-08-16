package com.example.franktastic4.mylifts.WorkoutListPackage;

import android.provider.BaseColumns;

/**
 * Created by Franktastic4 on 7/9/15.
 */
public final class WorkoutTableReaderContract {

    public WorkoutTableReaderContract(){

    }

     /* Inner class that defines the table contents */
     public static abstract class WorkoutTable implements BaseColumns {
         //BaseColumns give _ID
         public static final String TABLE_NAME = "WorkoutTable";
         public static final String WORKOUT_NAME = "Workout";
         public static final String EXERCISE_NAME = "Exercise";
         public static final String DATE = "Title";
         public static final String SET = "Sets";
         public static final String REPS = "Reps";
         public static final String WEIGHT = "Weight";
         public static final String DAYSELECTED = "DaySelected";
     }
}
