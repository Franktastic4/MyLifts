package com.example.franktastic4.mylifts.WorkoutListPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Franktastic4 on 6/12/15.
 */
public class ExerciseObjectArrayAdapter extends ArrayAdapter<ExerciseInstance> {

    private static class ViewHolder {
        private TextView itemView;
    }

    public ExerciseObjectArrayAdapter(Context context, int textViewResourceId, ArrayList<ExerciseInstance> items) {
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

        ExerciseInstance item = getItem(position);
        if (item!= null) {
            //this is us putting in the names on the list.
            viewHolder.itemView.setText(item.return_exercise_name());
        }

        return convertView;
    }

}
