package com.example.franktastic4.mylifts.MeasurementPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Franktastic4 on 5/3/15.
 */
public class MeasurementInstanceArrayAdapter extends ArrayAdapter<MeasurementInstance> {

    private static class ViewHolder {
        private TextView itemView;
    }

    public MeasurementInstanceArrayAdapter(Context context, int textViewResourceId, ArrayList<MeasurementInstance> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);

            viewHolder.itemView = (TextView) convertView.findViewById(android.R.id.text1);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MeasurementInstance item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            viewHolder.itemView.setText(item.return_measurement_instance_name());
        }

        return convertView;
    }
}
