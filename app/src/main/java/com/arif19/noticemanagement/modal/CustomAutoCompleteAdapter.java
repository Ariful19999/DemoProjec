package com.arif19.noticemanagement.modal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.arif19.noticemanagement.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAutoCompleteAdapter extends ArrayAdapter<add_item_for_group_admin> {

    private final List<add_item_for_group_admin> adminItemList;
    private final LayoutInflater layoutInflater;

    public CustomAutoCompleteAdapter(Context context, List<add_item_for_group_admin> adminItemList) {
        super(context, 0, adminItemList);
        this.adminItemList = adminItemList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.custom_dropdown_item, parent, false);
        }

        add_item_for_group_admin adminItem = adminItemList.get(position);

        ImageView adminImage = convertView.findViewById(R.id.image_view);
        TextView adminName = convertView.findViewById(R.id.text_view);

        adminName.setText(adminItem.getName());

        // Use Picasso or any other image loading library to load the image
        Picasso.get().load(adminItem.getImageUrl()).into(adminImage);

        return convertView;
    }
}
