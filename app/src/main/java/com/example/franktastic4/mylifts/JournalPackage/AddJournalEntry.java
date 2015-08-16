package com.example.franktastic4.mylifts.JournalPackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.franktastic4.mylifts.R;

import java.util.Calendar;

;

/**
 * Created by Franktastic4 on 7/22/15.
 */
public class AddJournalEntry extends android.support.v4.app.Fragment{

    Button addJournalEntry;
    EditText journalEntryField;

    JournalTableDbHelper mDbHelper;
    SQLiteDatabase JournalTableDB;

    public static AddJournalEntry newInstance() {
        AddJournalEntry fragment = new AddJournalEntry();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddJournalEntry() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new JournalTableDbHelper(getActivity().getApplicationContext());
        JournalTableDB = mDbHelper.getWritableDatabase();

    }

    @Override
    public void onResume(){
        super.onResume();
        //Its the toggle, go to activity
        ((JournalActivity)getActivity()).setActionBarTitle("Add Journal Entry");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_journal_entry, container, false);

        addJournalEntry = (Button) rootView.findViewById(R.id.AddJournalEntryFieldDone);
        journalEntryField = (EditText) rootView.findViewById(R.id.JournalEntryField);

        addJournalEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                helperMethod();

            }
        });

        return rootView;
    }


    public void helperMethod(){

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(journalEntryField.getWindowToken(), 0);


        if(!journalEntryField.getText().toString().equals("")){

            ContentValues values = new ContentValues();
            values.put(JournalTableReaderContract.JournalTable.CALENDAR, Calendar.getInstance().getTime().toString());
            values.put(JournalTableReaderContract.JournalTable.ENTRY, journalEntryField.getText().toString());
            JournalTableDB.insert(JournalTableReaderContract.JournalTable.TABLE_NAME, null, values);

        }

        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();


    }

}
