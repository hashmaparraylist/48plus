package com.akb48plus;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.akb48plus.common.image.ImageLoadTask;
import com.akb48plus.common.image.Utils;
import com.akb48plus.common.image.Utils.CacheBean;
import com.google.api.services.plus.model.Person;

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
                String url = member.getImage().getUrl();
                url = url.replaceAll("\\?sz\\=\\d+", "?sz=400");
                Log.d(TAG, "Profile photo: " + url);
                //InputStream is = (new URL(url)).openStream();
                CacheBean image = Utils.imageCache.get(url);
                if (null == image) {
                    imageview.setImageResource(R.drawable.dummy);
                    // Make a cache
                    image = Utils.buildCacheBean();
                    Utils.imageCache.put(url, image);
                    ImageLoadTask loader = new ImageLoadTask(imageview);
                    Log.d(TAG, "now caching image");
                    loader.execute(url);
                } else {
                    Log.d(TAG, "hitting, set cache image");
                    if (!image.isNotCaching()) {
                        Log.d(TAG, "now set image");
                        imageview.setImageDrawable(image.getImage());
                    }
                    
                }
                //Log.d(TAG, "Image has load");
            }
        }
        return view;
    }
}