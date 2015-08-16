package com.example.franktastic4.mylifts.WorkoutListPackage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Franktastic4 on 7/9/15.
 */
public class WorkoutTableDbHelper extends SQLiteOpenHelper {

        // If you change the database schema, you must increment the database version.

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "WorkoutTable.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + WorkoutTableReaderContract.WorkoutTable.TABLE_NAME + " (" +
                    WorkoutTableReaderContract.WorkoutTable._ID + " INTEGER PRIMARY KEY," +
                    WorkoutTableReaderContract.WorkoutTable.WORKOUT_NAME + TEXT_TYPE + COMMA_SEP +
                    WorkoutTableReaderContract.WorkoutTable.EXERCISE_NAME + TEXT_TYPE + COMMA_SEP +
                    WorkoutTableReaderContract.WorkoutTable.DATE + INT_TYPE + COMMA_SEP +
                    WorkoutTableReaderContract.WorkoutTable.SET + INT_TYPE + COMMA_SEP +
                    WorkoutTableReaderContract.WorkoutTable.REPS + INT_TYPE + COMMA_SEP +
                    WorkoutTableReaderContract.WorkoutTable.WEIGHT + INT_TYPE + COMMA_SEP +
                    WorkoutTableReaderContract.WorkoutTable.DAYSELECTED + TEXT_TYPE +  " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WorkoutTableReaderContract.WorkoutTable.TABLE_NAME;

    public WorkoutTableDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

