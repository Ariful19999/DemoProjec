package com.arif19.noticemanagement.recycleview;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;


import com.arif19.noticemanagement.R;

import java.util.ArrayList;

public class post_photos extends RecyclerView.Adapter<post_photos.ViewHolder> {

    private ArrayList<Bitmap> data;

    public post_photos(ArrayList<Bitmap> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_for_photos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap bitmap = data.get(position);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
        }
    }
}
