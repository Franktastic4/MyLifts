package com.example.franktastic4.mylifts.CalendarPackage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.franktastic4.mylifts.JournalPackage.JournalActivity;
import com.example.franktastic4.mylifts.R;

/**
 * Created by Franktastic4 on 7/23/15.
 */
public class CalendarFragment extends android.support.v4.app.Fragment {


    public CalendarFragment() {}

    @Override
    public void onResume() {
        ((CalendarActivity) getActivity()).setActionBarTitle("Calendar");
        //Have to set title bc i return to this page
        super.onResume();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar_fragment, container, false);

        final CalendarView myCalendarViewHandle = (CalendarView) rootView.findViewById(R.id.calenderViewID);

        //comes in milliseconds
        //myCalendarViewHandle.setMaxDate());

        myCalendarViewHandle.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {

                Intent calendarToJournal = new Intent(getActivity(), JournalActivity.class);
                calendarToJournal.putExtra("calendarToJournalBool", true);
                calendarToJournal.putExtra("yearKey", year);
                calendarToJournal.putExtra("monthKey", month);
                calendarToJournal.putExtra("dayOfMonthKey", dayOfMonth);
                startActivity(calendarToJournal);

            }
        });


        return rootView;
    }

}
