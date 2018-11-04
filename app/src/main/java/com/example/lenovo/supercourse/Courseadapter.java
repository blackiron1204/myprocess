package com.example.lenovo.supercourse;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Courseadapter extends ArrayAdapter<Course> {
    private Context context;
    private int resource;
    private List<Course> courses;
    public Courseadapter(@NonNull Context context, int resource, List<Course> courses
    ) {
        super(context, resource,courses);
        this.context=context;
        this.resource=resource;
        this.courses=courses;
        Log.i("zm1",courses.size()+"");
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Nullable
    @Override
    public Course getItem(int position) {
        return courses.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(resource,parent,false);
        }
        TextView monday=convertView.findViewById(R.id.monday);
        TextView tuesday=convertView.findViewById(R.id.tuesday);
        TextView wednesday=convertView.findViewById(R.id.wednesday);
        TextView thursday=convertView.findViewById(R.id.thursday);
        TextView friday=convertView.findViewById(R.id.friday);
        TextView saturday=convertView.findViewById(R.id.saturday);
        TextView sunday=convertView.findViewById(R.id.sunday);

        monday.setText(getItem(position).getMonday());
        tuesday.setText(getItem(position).getTuesday());
        wednesday.setText(getItem(position).getWednesday());
        thursday.setText(getItem(position).getThursday());
        friday.setText(getItem(position).getFriday());
        saturday.setText(getItem(position).getSaturday());
        sunday.setText(getItem(position).getSunday());

        return convertView;
    }

}

