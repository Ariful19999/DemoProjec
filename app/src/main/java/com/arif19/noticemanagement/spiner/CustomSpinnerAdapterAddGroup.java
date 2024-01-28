package com.arif19.noticemanagement.spiner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.arif19.noticemanagement.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomSpinnerAdapterAddGroup extends ArrayAdapter<CustomSpinnerItemAddGroup> implements Filterable {

    private LayoutInflater inflater;
    private List<CustomSpinnerItemAddGroup> originalItems;
    private List<CustomSpinnerItemAddGroup> filteredItems;

    public CustomSpinnerAdapterAddGroup(Context context, JSONArray jsonArray) throws JSONException {
        super(context, 0, getItems(jsonArray));
        inflater = LayoutInflater.from(context);
        originalItems = getItems(jsonArray);
        filteredItems = new ArrayList<>(originalItems);
    }

    private static List<CustomSpinnerItemAddGroup> getItems(JSONArray jsonArray) throws JSONException {
        List<CustomSpinnerItemAddGroup> items = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String text = jsonObject.getString("name");
            String imageUrl = jsonObject.getString("img_url");
            items.add(new CustomSpinnerItemAddGroup(text, imageUrl));
        }
        return items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.add_group_spinner_item, parent, false);
        }

        CustomSpinnerItemAddGroup item = getItem(position);
        if (item != null) {
            TextView textView = view.findViewById(R.id.spinner_text);
            ImageView imageView = view.findViewById(R.id.spinner_image);

            textView.setText(item.getText());
            // Load image using Picasso library
            Picasso.get().load(item.getImageUrl()).into(imageView);
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<CustomSpinnerItemAddGroup> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalItems);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (CustomSpinnerItemAddGroup item : originalItems) {
                        if (item.getText().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredItems.clear();
                filteredItems.addAll((List<CustomSpinnerItemAddGroup>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @Nullable
    @Override
    public CustomSpinnerItemAddGroup getItem(int position) {
        return filteredItems.get(position);
    }
}
