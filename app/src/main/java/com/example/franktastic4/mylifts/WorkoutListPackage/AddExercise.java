package com.example.franktastic4.mylifts.WorkoutListPackage;

/**
 * Created by Franktastic4 on 6/13/15.
 */

import android.content.ContentValues;
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

import com.example.franktastic4.mylifts.NavigationDrawerFragment;
import com.example.franktastic4.mylifts.R;


public class AddExercise extends android.support.v4.app.Fragment {



    public EditText new_exercise_field;
    String lastWorkoutName;
    int lastPositionAddExercise;
    private TextView addExerciseTextViewMsg;
    private ExerciseList myExerciseListFragment;
    //public WorkoutList.Workout_List_Fragment myWorkoutListFragment;

    public static AddExercise newInstance() {
        AddExercise fragment = new AddExercise();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddExercise() {
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

        myExerciseListFragment = (ExerciseList)getFragmentManager().findFragmentByTag("exercise_fragment_tag");
        ((WorkoutList) getActivity()).setActionBarTitle("Add " + myExerciseListFragment.return_workout_name() + " Exercise");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_add_exercise, container, false);
        addExerciseTextViewMsg = (TextView)rootView.findViewById(R.id.addExerciseTextView);
        addExerciseTextViewMsg.setText("An exercise is a component of a workout. For instance for a chest Workout, you might do Bench Press");

        Button AddExerciseDoneButton = (Button) rootView.findViewById(R.id.add_exercise_button); //possibly returning NULL b/c it cant find
        AddExerciseDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText new_exercise_field;
                new_exercise_field = (EditText) rootView.findViewById(R.id.add_exercise_message);
                addExerciseDone(v);
            }
        });

        return rootView;
    }

    public void addExerciseDone(View view){

        //set up handle on the workout name variable
        //Done above

        myExerciseListFragment = (ExerciseList) getFragmentManager().findFragmentByTag("exercise_fragment_tag");

        if(!new_exercise_field.getText().toString().equals("")) {
            //set up exerciseInstance object
            ExerciseInstance myExerciseInstance;
            myExerciseInstance = ExerciseInstance.newInstance(new_exercise_field.getText().toString(), myExerciseListFragment.return_workout_name());

            //is a problem on my actual phone -- Try this.
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(new_exercise_field.getWindowToken(), 0);

            //add exercise object to list of exercises

            ContentValues values = new ContentValues();
            values.put(WorkoutTableReaderContract.WorkoutTable.WORKOUT_NAME, myExerciseListFragment.return_workout_name());
            values.put(WorkoutTableReaderContract.WorkoutTable.EXERCISE_NAME , new_exercise_field.getText().toString());
            values.put(WorkoutTableReaderContract.WorkoutTable.DATE, "2");
            myExerciseListFragment.WorkoutTableDb.insert(WorkoutTableReaderContract.WorkoutTable.TABLE_NAME, null, values);

            myExerciseListFragment.ExerciseNames.add(myExerciseInstance);



            //myExerciseListFragment.printDatabase();
        }


        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();

        myExerciseListFragment.mAdapter.notifyDataSetChanged();
        myExerciseListFragment.lvFragmentExerciseList.invalidate();

    }

}
