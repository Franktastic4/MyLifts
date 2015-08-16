package com.example.franktastic4.mylifts.WorkoutListPackage;

import java.util.Calendar;

/**
 * Created by Franktastic4 on 6/18/15.
 */
public class ExerciseObject {

    private String set;
    private String weight;
    private String reps;
    private long restTime;
    private Calendar timeStamp;


    public void setSet(String newSetValue){set = newSetValue;}

    public void setReps(String newRepValue){reps = newRepValue;  }

    public void setWeight(String newWeightValue){weight = newWeightValue;}

    public void setDate(Calendar currentDate){timeStamp = currentDate;}

    public void setRestTime(long newRestTime){ restTime = newRestTime; }

    public String returnSet(){return set;}

    public String returnReps(){ return reps; }

    public String returnWeight(){ return weight;}

    public int returnMonth(){
        return timeStamp.get(Calendar.MONTH) + 1;
    }

    public int returnDay(){
        return timeStamp.get(Calendar.DAY_OF_MONTH);
    }

    public Calendar returnTimeStamp(){return timeStamp;}

    public long returnRestTime(){ return restTime; }



}
