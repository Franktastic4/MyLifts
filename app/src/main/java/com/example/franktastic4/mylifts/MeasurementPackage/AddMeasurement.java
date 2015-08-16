package com.example.franktastic4.mylifts.MeasurementPackage;

/**
 * Created by Franktastic4 on 6/29/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.franktastic4.mylifts.WorkoutListPackage.AddWorkout;
import com.example.franktastic4.mylifts.NavigationDrawerFragment;
import com.example.franktastic4.mylifts.R;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link AddWorkout#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddMeasurement extends android.support.v4.app.Fragment {
    private static TextView addMeasurementsTextViewMsg;

    public static AddMeasurement newInstance() {
        AddMeasurement fragment = new AddMeasurement();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddMeasurement() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        super.onResume();
        //Its the toggle, go to activity
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("Add Measurement Item");
        if(MeasurementsActivity.mMeasurementActivityIsFront) {
            NavigationDrawerFragment.plusButton.setVisibility(View.GONE);
            NavigationDrawerFragment.minusButton.setVisibility(View.GONE);
            NavigationDrawerFragment.undoTappedButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Add Measurement Item");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_measurement_item, container, false);
        addMeasurementsTextViewMsg = (TextView) rootView.findViewById(R.id.addMeasurementTextView);
        addMeasurementsTextViewMsg.setText("A example of a measurement object would be calf and bicep size or (lean) weight");

        return rootView;
    }



}
