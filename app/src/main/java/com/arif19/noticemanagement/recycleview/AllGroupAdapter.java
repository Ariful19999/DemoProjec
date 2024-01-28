package com.arif19.noticemanagement.recycleview;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.arif19.noticemanagement.R;
import com.arif19.noticemanagement.modal.AllGroupItem;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllGroupAdapter extends RecyclerView.Adapter<AllGroupAdapter.ViewHolder> {

    private List<AllGroupItem> AllGroupItems; // Replace AllGroupItem with your actual data model
    private Context context;
    private OnAddMemberClickListener onAddMemberClickListener; // Add this line

    public AllGroupAdapter(Context context, List<AllGroupItem> AllGroupItems) {
        this.context = context;
        this.AllGroupItems = AllGroupItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_group_custom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllGroupItem currentPost = AllGroupItems.get(position);

        String avatarImageUrl = currentPost.getAvatarImage();
        Glide.with(context)
                .load(avatarImageUrl)
                .centerCrop()
                .placeholder(R.drawable.group_avatar)
                .error(R.drawable.group_avatar)
                .into(holder.avatarImage);

        holder.groupName.setText(currentPost.getReporterName());
        holder.groupId.setText(currentPost.getgroupId());

        // Set the button state based on the isAdded flag
        if (currentPost.isAdded()) {
//            holder.addMemberButton.setEnabled(false);
            holder.group.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray)); // Change background color

        } else {
//            holder.addMemberButton.setEnabled(true);
            holder.group.setBackgroundResource(R.drawable.border_bottom);

        }
    }

    @Override
    public int getItemCount() {
        return AllGroupItems.size();
    }

    public void setOnAddMemberClickListener(OnAddMemberClickListener listener) {
        this.onAddMemberClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView groupId;
        CircleImageView avatarImage;
        Button addMemberButton;
        LinearLayout group;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.groupImage);
            groupName = itemView.findViewById(R.id.groupName);
            groupId = itemView.findViewById(R.id.groupId);
            group = itemView.findViewById(R.id.group);

            group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && onAddMemberClickListener != null) {
                        onAddMemberClickListener.onAddMemberClick(AllGroupItems.get(getAdapterPosition()).getgroupId());

                        // Update the isAdded flag and refresh the adapter
                        if (AllGroupItems.get(getAdapterPosition()).isAdded()){
                            AllGroupItems.get(getAdapterPosition()).setAdded(false);
                        }else{
                            AllGroupItems.get(getAdapterPosition()).setAdded(true);
                        }

                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    // Interface for click listener
    public interface OnAddMemberClickListener {
        void onAddMemberClick(String memberId);
    }
}
