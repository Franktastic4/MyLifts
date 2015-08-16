package com.example.franktastic4.mylifts.AnalysisPackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Franktastic4 on 7/28/15.
 */
public class ExpandableListHeaderObject {

    List<String> myList = new ArrayList<String>();
    String nameOfExpandableListHeaderObject = null;

    public void setName(String newName){
        nameOfExpandableListHeaderObject = newName;
    }

    public String returnName(){
        return nameOfExpandableListHeaderObject;
    }

    public void add(String newString){
        myList.add(newString);
    }

    public List<String> returnMyList(){
        return myList;
    }

}
