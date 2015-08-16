package com.example.franktastic4.mylifts.JournalPackage;

import android.provider.BaseColumns;

/**
 * Created by Franktastic4 on 7/22/15.
 */
public class JournalTableReaderContract {

    public JournalTableReaderContract(){

    }
    /* Inner class that defines the table contents */
    public static abstract class JournalTable implements BaseColumns {
        //BaseColumns give _ID
        public static final String TABLE_NAME = "JournalTable";
        public static final String CALENDAR = "Calender";
        public static final String ENTRY = "Entry";
    }


}
