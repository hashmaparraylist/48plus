package com.akb48plus;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.akb48plus.common.Const;
import com.akb48plus.common.Utils;
import com.akb48plus.common.cache.PeopleWrapper;
import com.akb48plus.common.image.ImageLoader;
import com.akb48plus.common.model.Model;
import com.akb48plus.common.model.People;

public class MemberListAdapter extends ArrayAdapter<Model> {
    private static final String TAG = MemberListAdapter.class.getName();
    public ImageLoader imageLoader;
    private Context context;

    public MemberListAdapter(Context ctx, List<Model> people) {
        super(ctx, android.R.layout.simple_spinner_item, people);
        setDropDownViewResource(R.layout.profile_list);
        imageLoader=new ImageLoader(ctx);
        this.context = ctx;
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

        People member = (People) getItem(position);
        if (member != null) {
            Log.d(TAG, "Profile name: " + member.getDisplayName());
            TextView textView = (TextView) view.findViewById(R.id.txtProfileName);
            if (textView != null) {
                textView.setText(member.getDisplayName());
            }
            TextView textId = (TextView) view.findViewById(R.id.txtProfileId);
            if (textId != null) {
                textId.setText(member.getId());
            }
            // Uri uri = Uri.parse(member.getImage().getUrl());
            ImageView imageview = (ImageView) view.findViewById(R.id.imgProfilePhoto);
            if (imageview != null) {
                Log.d(TAG, "Profile photo: " + member.getProfileUrl());
                String url = member.getProfileUrl();
                if ("".equals(url)) {
                    try {
                        PeopleWrapper wrapper = new PeopleWrapper(this.context);
                        List<Model> list = wrapper.get(member.getId());
                        Model model = list.get(0);
                        url = ((People) model).getProfileUrl();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                imageLoader.displayImage(
                        Utils.changePhotoSizeInUrl(member.getProfileUrl(), Const.PHOTO_SIZE),
                        imageview);
            }
        }
        return view;
    }
}