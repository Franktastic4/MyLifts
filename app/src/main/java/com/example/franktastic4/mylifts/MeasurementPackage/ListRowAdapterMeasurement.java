package com.example.franktastic4.mylifts.MeasurementPackage;

/**
 * Created by Franktastic4 on 7/2/15.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.franktastic4.mylifts.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.franktastic4.mylifts.ListViewRowConstants.FIRST_COLUMN;
import static com.example.franktastic4.mylifts.ListViewRowConstants.SECOND_COLUMN;
import static com.example.franktastic4.mylifts.ListViewRowConstants.THIRD_COLUMN;


public class ListRowAdapterMeasurement extends BaseAdapter
{
    MeasurementInstance myMeasurementsInstance;
    public ArrayList<HashMap> list;
    Activity activity;
    Boolean thirdColBool = false;


    public ListRowAdapterMeasurement(Activity activity, ArrayList<HashMap> list, Boolean thirdColBoolean) {
        super();
        this.activity = activity;
        this.list = list;
        thirdColBool = thirdColBoolean;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater =  activity.getLayoutInflater();

        if (convertView == null){

         if(thirdColBool){
                convertView = inflater.inflate(R.layout.list_view_row_measurement_thirdcol,null);
            }else{
                convertView = inflater.inflate(R.layout.list_view_row_measurement,null);
            }

            holder = new ViewHolder();
            holder.txtFirst = (TextView) convertView.findViewById(R.id.FirstText);
            holder.txtSecond = (TextView) convertView.findViewById(R.id.SecondText);

            if(thirdColBool){
                holder.txtThird = (TextView) convertView.findViewById(R.id.ThirdText);
            }
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }


        HashMap map = list.get(position);

        holder.txtFirst.setText(map.get(FIRST_COLUMN).toString());
        holder.txtSecond.setText(map.get(SECOND_COLUMN).toString());

        //thirdCol
        if(thirdColBool) {

            //map at third column is empty. Why?
            //ERROR IS ON PRE EXISTING ones that were added

            //Log.d("SOME TAG", list.get(position).toString());

            if(map.get(THIRD_COLUMN) == null){
                holder.txtThird.setText("Pre-existing");

            }else{
                holder.txtThird.setText(map.get(THIRD_COLUMN).toString());
            }

        }


        return convertView;
    }

}