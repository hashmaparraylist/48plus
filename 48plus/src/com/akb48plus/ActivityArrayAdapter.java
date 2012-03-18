/**
 * 
 */
package com.akb48plus;


import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.akb48plus.common.cache.PostWrapper;
import com.akb48plus.common.image.ImageLoader;
import com.akb48plus.common.model.Model;

/**
 * @author QuSheng
 * 
 */
public class ActivityArrayAdapter extends ArrayAdapter<Model> {
    
    public ImageLoader imageLoader;
    private static String TAG = ActivityArrayAdapter.class.getName();
    
    public ActivityArrayAdapter(Context context, List<Model> activities) {
        super(context, android.R.layout.simple_spinner_item, activities);
        setDropDownViewResource(R.layout.activities_list);
        imageLoader = new ImageLoader(context);
        
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        Log.d(TAG, "Making List");
        if (view == null) {
            final LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activities_list, parent, false);
        }

        final Model activity = getItem(position);
        if (null == activity) {
            return view;
        }
        
        Log.d(TAG, "Now Activity ID: " + activity.getId());
        try {
            PostWrapper wrapper = new PostWrapper(getContext());
            wrapper.adapter(view, activity, imageLoader);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return view;
    }
}
