package com.example.franktastic4.mylifts.SettingsPackage;

/**
 * Created by Franktastic4 on 7/17/15.
 */
public class WorkoutTableObject {

    private String workoutName;
    private String exerciseName;
    private String calenderDate;
    private String set;
    private String reps;
    private String weight;


    public void setWorkoutName(String newString){
        workoutName = newString;
    }

    public void setExerciseName(String newString){
        exerciseName = newString;
    }

    public void setCalenderDate(String newString){
        calenderDate = newString;
    }

    public void setSet(String newString){
        set = newString;
    }

    public void setReps(String newString){
        reps = newString;
    }

    public void setWeight(String newString){
        weight = newString;
    }

    public String getWorkoutName(){
        return workoutName;
    }

    public String getExerciseName(){
        return exerciseName;
    }

    public String getCalenderDate(){
        return calenderDate;
    }

    public String getSet(){
        return set;
    }

    public String getReps(){
        return reps;
    }

    public String getWeight(){
        return weight;
    }



}
