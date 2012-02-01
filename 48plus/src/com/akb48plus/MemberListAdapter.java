package com.akb48plus;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.api.services.plus.model.Person;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberListAdapter extends ArrayAdapter<Person> {
    private static final String TAG = MemberListAdapter.class.getName();
    
    public MemberListAdapter(Context ctx, List<Person> people) {
        super(ctx, android.R.layout.simple_spinner_item, people);
        setDropDownViewResource(R.layout.profile_list);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        return getDropDownView(position, view, parent);
    }
    
    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null) {
            final LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.profile_list, parent, false);
        }

        Person member = getItem(position);
        if (member != null) {
            //LinearLayout profile = (LinearLayout) view.findViewById(R.id.profile);
            Log.d(TAG, "Profile name: " + member.getDisplayName());
            TextView textView = (TextView) view.findViewById(R.id.txtProfileName);
            if (textView != null) {
                textView.setText(member.getDisplayName());
            }
            //Uri uri = Uri.parse(member.getImage().getUrl());
            ImageView imageview = (ImageView) view.findViewById(R.id.imgProfilePhoto);
            if (imageview != null) {
                try {
                    String url = member.getImage().getUrl();
                    url = url.replaceAll("\\?sz\\=\\d+", "?sz=400");
                    Log.d(TAG, "Profile photo: " + url);
                    InputStream is = (new URL(url)).openStream();
                    imageview.setImageDrawable(Drawable.createFromStream(is, "imgsrc.jpg"));
                    Log.d(TAG, "Image has load");
                } catch (MalformedURLException e) {
                    Log.e(TAG,e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG,e.getMessage());
                }
            }
        }
        return view;
    }
}