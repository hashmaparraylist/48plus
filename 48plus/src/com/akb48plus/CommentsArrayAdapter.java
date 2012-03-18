/**
 * 
 */
package com.akb48plus;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.akb48plus.common.model.Comment;
import com.akb48plus.common.model.Model;

/**
 * @author QuSheng
 *
 */
public class CommentsArrayAdapter extends ArrayAdapter<Model> {

    public CommentsArrayAdapter(Context context, List<Model> list) {
        super(context, android.R.layout.simple_spinner_item, list);
        setDropDownViewResource(R.layout.comments_list);
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
            view = inflater.inflate(R.layout.comments_list, parent, false);
        }
        
        Comment comment = (Comment) getItem(position);
        if (null == comment) {
            return view;
        }
        
        TextView txtDisplayName = (TextView) view.findViewById(R.id.txtDisplayName);
        TextView txtContent = (TextView) view.findViewById(R.id.txtContent);
        
        txtDisplayName.setText(comment.getDisplayName());
        txtContent.setText(Html.fromHtml(comment.getContent()));
        return view;
    }

}
