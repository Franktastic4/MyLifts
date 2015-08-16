package com.example.franktastic4.mylifts.MeasurementPackage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.franktastic4.mylifts.MeasurementPackage.MeasurementTableReaderContract.MeasurementTable;



/**
 * Created by Franktastic4 on 7/13/15.
 */
public class MeasurementTableDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MeasurementTable.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MeasurementTable.TABLE_NAME + " (" +
                    MeasurementTable._ID + " INTEGER PRIMARY KEY," +
                    MeasurementTable.MEASUREMENT + TEXT_TYPE + COMMA_SEP +
                    MeasurementTable.MEASUREMENT_VALUE + TEXT_TYPE + COMMA_SEP +
                    MeasurementTable.CALENDAR + TEXT_TYPE + COMMA_SEP +
                    MeasurementTable.GOAL_START + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MeasurementTable.TABLE_NAME;

    public MeasurementTableDbHelper(Context context) {
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
