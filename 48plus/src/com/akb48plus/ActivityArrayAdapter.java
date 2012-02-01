/**
 * 
 */
package com.akb48plus;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.api.services.plus.model.Activity;

/**
 * @author QuSheng
 * 
 */
public class ActivityArrayAdapter extends ArrayAdapter<Activity> {
    public ActivityArrayAdapter(Context context, List<Activity> people) {
        super(context, android.R.layout.simple_spinner_item, people);
        setDropDownViewResource(R.layout.main);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null) {
            final LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.main, parent, false);
        }

        final Activity activity = getItem(position);
        if (null == activity) {
            return view;
        }
        
        ((TextView) view.findViewById(R.id.title)).setText(activity.getTitle());
        return view;
    }
}
