package com.example.franktastic4.mylifts.WorkoutListPackage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.franktastic4.mylifts.NavigationDrawerFragment;
import com.example.franktastic4.mylifts.R;


/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link AddWorkout#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddWorkout extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static TextView addWorkoutTextViewMsg;
    private WorkoutList.Workout_List_Fragment myWorkoutListFragment;
    private int lastWorkoutIndex;
    Spinner dropdown;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    // TODO: Rename and change types and number of parameters
    public static AddWorkout newInstance(String param1, String param2) {
        AddWorkout fragment = new AddWorkout();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AddWorkout() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        super.onResume();

        if(WorkoutList.mActivityIsFront) {
            NavigationDrawerFragment.plusButton.setVisibility(View.GONE);
            NavigationDrawerFragment.minusButton.setVisibility(View.GONE);
            NavigationDrawerFragment.undoTappedButton.setVisibility(View.GONE);
        }

        ((WorkoutList) getActivity()).setActionBarTitle("Add Workout");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View rootView = inflater.inflate(R.layout.fragment_add_workout, container, false);

        if(WorkoutList.mActivityIsFront) {
            NavigationDrawerFragment.plusButton.setVisibility(View.GONE);
            NavigationDrawerFragment.minusButton.setVisibility(View.GONE);
            NavigationDrawerFragment.undoTappedButton.setVisibility(View.GONE);
        }

        dropdown = (Spinner)rootView.findViewById(R.id.spinner1);
        String[] items = new String[]{"None","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);

        addWorkoutTextViewMsg = (TextView) rootView.findViewById(R.id.addWorkoutTextView);
        addWorkoutTextViewMsg.setText("A workout is a group of exercises that are designed to workout a system of muscles, for example a Chest Workout might include a Bench Press exercise");

        return rootView;
    }



}
