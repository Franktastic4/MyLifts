package com.example.franktastic4.mylifts.MeasurementPackage;

import java.util.Calendar;

/**
 * Created by Franktastic4 on 7/6/15.
 */
public class MeasurementObject {
    private double measurmentValue = 0;
    private Calendar timeStamp;
    private boolean isGoalStart = false;

    void setMeasurmentValue(double value){measurmentValue = value;}
    void setTimeStamp(Calendar time){timeStamp = time;}
    void setIsGoalStart(Boolean bool){isGoalStart = bool;}

    double returnMeasurementValue(){return measurmentValue;}
    Calendar returnTimeStamp(){return timeStamp;}
    boolean isGoalStart(){return isGoalStart;}

    public int returnMonth(){
        return timeStamp.get(Calendar.MONTH) + 1;
    }
    public int returnDay(){
        return timeStamp.get(Calendar.DAY_OF_MONTH);
    }
}
