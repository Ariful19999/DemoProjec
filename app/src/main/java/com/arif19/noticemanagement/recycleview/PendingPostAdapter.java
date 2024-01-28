package com.arif19.noticemanagement.recycleview;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.arif19.noticemanagement.R;
import com.arif19.noticemanagement.modal.PendingPostItem;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PendingPostAdapter extends RecyclerView.Adapter<PendingPostAdapter.ViewHolder> {

    private static List<PendingPostItem> PendingPostItems; // Replace PendingPostItem with your actual data model
    private Context context;
    private static AddGroupMemberAdapter.OnAddMemberClickListener onAddMemberClickListener; // Add this line

    public PendingPostAdapter(Context context, List<PendingPostItem> PendingPostItems) {
        this.context = context;
        this.PendingPostItems = PendingPostItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_pending_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Retrieve the current PendingPostItem from the list
        PendingPostItem currentPost = PendingPostItems.get(position);



        // Set reporter's name
        holder.postTitle.setText(currentPost.getReporterName());

        // Set post text
        holder.textPost.setText(currentPost.getPostText());

        // Check if there are images to display
        List<String> imageUrls = currentPost.getImageUrls(); // Get image URLs for the post

        if (imageUrls != null && !imageUrls.isEmpty()) {
            // Show the layout for multiple images
            holder.layoutImages.setVisibility(View.VISIBLE);

            // Clear any previous ImageViews
            holder.layoutImages.removeAllViews();

            // Create ImageViews and load images into them using Glide
            for (String imageUrl : imageUrls) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        600, 400); // Set your desired width and height here
                layoutParams.setMargins(5, 0, 5, 5);
                imageView.setLayoutParams(layoutParams);

                // Load image into the ImageView using Glide
                Glide.with(context)
                        .load(imageUrl)
                        //.placeholder(R.drawable.placeholder_image) // Placeholder image
                        //.error(R.drawable.error_image) // Error image if loading fails
                        .centerCrop()
                        .into(imageView);

                // Add the ImageView to the layout
                holder.layoutImages.addView(imageView);
            }
        } else {
            // Hide the layout for multiple images if no images are present
            holder.layoutImages.setVisibility(View.GONE);
        }

        // Check if the post contains a video URL
        if (currentPost.getVideoUrl() != null && !currentPost.getVideoUrl().isEmpty()) {
            holder.videoView.setVisibility(View.VISIBLE);
            String videoPath =currentPost.getVideoUrl();
            // String videoPath = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_50mb%20-%20Copy.mp4";
            // Convert the path to a Uri to play the video
            Uri uri = Uri.parse(videoPath);
            holder.videoView.setVideoURI(uri);

            //  holder.videoView.setVideoPath(currentPost.getVideoUrl());

            holder.videoView.requestFocus();

            // Create an instance of MediaController using the provided context
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.videoView);
            // Set MediaController to the VideoView
            holder.videoView.setMediaController(mediaController);
            // holder.videoView.start();

        } else {
            holder.videoView.setVisibility(View.GONE);
        }

        /// set reporter image

        String avatarImageUrl = currentPost.getAvatarImage();

        Glide.with(context)
                .load(avatarImageUrl)
                .centerCrop()
                .placeholder(R.drawable.single_avatar)
                .error(R.drawable.single_avatar)
                .into(holder.avatarImage);


        // Set the button state based on the isAdded flag
        if (currentPost.isAdded()) {
//            holder.addMemberButton.setEnabled(false);
            holder.selectButton.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray)); // Change background color
            holder.selectButton.setTextColor(ContextCompat.getColor(context, android.R.color.white)); // Change text color
            holder.selectButton.setText("selected");
        } else {
//            holder.addMemberButton.setEnabled(true);
            holder.selectButton.setBackgroundResource(R.drawable.radius);
            holder.selectButton.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.selectButton.setText("select");
        }

    }

    @Override
    public int getItemCount() {
        return PendingPostItems.size();
    }

    public void setOnAddMemberClickListener(AddGroupMemberAdapter.OnAddMemberClickListener listener) {
        this.onAddMemberClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle;
        TextView textPost;
        LinearLayout layoutImages;
        VideoView videoView;
        CircleImageView avatarImage;
        Button selectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.postAvatar);;
            postTitle = itemView.findViewById(R.id.postTitle);
            textPost = itemView.findViewById(R.id.textPost);
            layoutImages = itemView.findViewById(R.id.layoutImages);
            videoView = itemView.findViewById(R.id.videoView);

            selectButton = itemView.findViewById(R.id.selectButton);

            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && onAddMemberClickListener != null) {
                        onAddMemberClickListener.onAddMemberClick(PendingPostItems.get(getAdapterPosition()).getPostId());

                        // Update the isAdded flag and refresh the adapter
                        if (PendingPostItems.get(getAdapterPosition()).isAdded()){
                            PendingPostItems.get(getAdapterPosition()).setAdded(false);
                        }else{
                            PendingPostItems.get(getAdapterPosition()).setAdded(true);
                        }

                        notifyDataSetChanged();
                    }
                }
            });
            // Other view initialization...
        }
    }
    // Interface for click listener
    public interface OnAddMemberClickListener {
        void onAddMemberClick(String memberId);
    }
}

