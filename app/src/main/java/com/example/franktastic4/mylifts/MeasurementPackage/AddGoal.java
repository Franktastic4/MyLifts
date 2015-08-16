package com.example.franktastic4.mylifts.MeasurementPackage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franktastic4.mylifts.NavigationDrawerFragment;
import com.example.franktastic4.mylifts.R;

/**
 * Created by Franktastic4 on 7/6/15.
 */
public class AddGoal extends android.support.v4.app.Fragment {

    private static TextView addMeasurementsTextViewMsg;
    private EditText goalWeight;
    private EditText goalDays;
    private EditText currentMeasurement;
    private Button doneButton;
    private FragmentManager fm;
    private MeasurementInstance myMeasurementsInstance;

    public static AddGoal newInstance() {
        AddGoal fragment = new AddGoal();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddGoal() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        super.onResume();
        //Its the toggle, go to activity
        ((MeasurementsActivity)getActivity()).setActionBarTitle("Add a Goal");
        if(MeasurementsActivity.mMeasurementActivityIsFront) {
            NavigationDrawerFragment.plusButton.setVisibility(View.GONE);
            NavigationDrawerFragment.minusButton.setVisibility(View.GONE);
            NavigationDrawerFragment.undoTappedButton.setVisibility(View.GONE);
        }
    }

    public static boolean isNumericOne(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_goal, container, false);

        fm = getActivity().getSupportFragmentManager();
        goalDays = (EditText) rootView.findViewById(R.id.add_goal_date_edit_text);
        goalWeight = (EditText) rootView.findViewById(R.id.add_goal_edit_text);
        currentMeasurement = (EditText) rootView.findViewById(R.id.currentMeasurementEditText);

        doneButton = (Button) rootView.findViewById(R.id.add_goal_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (goalWeight.getText().toString().equals("") || currentMeasurement.getText().toString().equals("")) {
                    fm.popBackStack();
                    Toast.makeText(getActivity().getApplicationContext(), "Incomplete, did not add goal", Toast.LENGTH_SHORT).show();

                }else{

                    if(isNumericOne(goalDays.getText().toString()) && isNumericOne(goalWeight.getText().toString()) && isNumericOne(currentMeasurement.getText().toString())){
                        addGoalDone();

                    }else {
                        fm.popBackStack();
                        Toast.makeText(getActivity().getApplicationContext(), "Non numeric input, did not add goal", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        return rootView;
    }

    void addGoalDone() {

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(currentMeasurement.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(goalWeight.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(goalDays.getWindowToken(), 0);

        myMeasurementsInstance = (MeasurementInstance) getFragmentManager().findFragmentByTag("measurements_instance_tag");
        double goalWeightVar = Double.parseDouble(goalWeight.getText().toString());
        double currentMesVar = Double.parseDouble(currentMeasurement.getText().toString());

        if (goalDays.getText().toString().equals("")) {
            myMeasurementsInstance.setMyGoalCalendar(10000);
        }else{
            myMeasurementsInstance.setMyGoalCalendar(Integer.parseInt(goalDays.getText().toString()));
        }

        if(goalWeightVar < currentMesVar){
            //if goal is higher than current, want to gain
            myMeasurementsInstance.setGoalIsToLoseWeight(true);
        }

        myMeasurementsInstance.setMyGoalValue(goalWeightVar);
        //myMeasurementsInstance.setGoalBoolean(true);
        //I don't know if the following is being called
        fm.popBackStack();
        myMeasurementsInstance.addMeasurementObject(currentMesVar);
        //fm.popBackStack();
    }

}
