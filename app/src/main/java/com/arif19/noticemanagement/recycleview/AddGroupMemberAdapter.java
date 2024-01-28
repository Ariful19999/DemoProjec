package com.arif19.noticemanagement.recycleview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.arif19.noticemanagement.R;
import com.arif19.noticemanagement.modal.AddGroupMemberItem;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupMemberAdapter extends RecyclerView.Adapter<AddGroupMemberAdapter.ViewHolder> {

    private List<AddGroupMemberItem> addGroupMemberItems; // Replace AddGroupMemberItem with your actual data model
    private Context context;
    private OnAddMemberClickListener onAddMemberClickListener; // Add this line

    public AddGroupMemberAdapter(Context context, List<AddGroupMemberItem> addGroupMemberItems) {
        this.context = context;
        this.addGroupMemberItems = addGroupMemberItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_group_member_custom, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddGroupMemberItem currentPost = addGroupMemberItems.get(position);

        String avatarImageUrl = currentPost.getAvatarImage();
        Glide.with(context)
                .load(avatarImageUrl)
                .centerCrop()
                .placeholder(R.drawable.single_avatar)
                .error(R.drawable.single_avatar)
                .into(holder.avatarImage);

        holder.memberName.setText(currentPost.getReporterName());
        holder.memberId.setText(currentPost.getMemberId());

        // Set the button state based on the isAdded flag
        if (currentPost.isAdded()) {
//            holder.addMemberButton.setEnabled(false);
            holder.addMemberButton.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray)); // Change background color
            holder.addMemberButton.setTextColor(ContextCompat.getColor(context, android.R.color.white)); // Change text color
            holder.addMemberButton.setText("selected");
        } else {
//            holder.addMemberButton.setEnabled(true);
            holder.addMemberButton.setBackgroundResource(R.drawable.radius);
            holder.addMemberButton.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.addMemberButton.setText("select");
        }
    }

    @Override
    public int getItemCount() {
        return addGroupMemberItems.size();
    }

    public void setOnAddMemberClickListener(OnAddMemberClickListener listener) {
        this.onAddMemberClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView memberName;
        TextView memberId;
        CircleImageView avatarImage;
        Button addMemberButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.memberImage);
            memberName = itemView.findViewById(R.id.memberName);
            memberId = itemView.findViewById(R.id.memberId);
            addMemberButton = itemView.findViewById(R.id.addMember);

            addMemberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getAdapterPosition() != RecyclerView.NO_POSITION && onAddMemberClickListener != null) {
                        onAddMemberClickListener.onAddMemberClick(addGroupMemberItems.get(getAdapterPosition()).getMemberId());

                        // Update the isAdded flag and refresh the adapter
                        if (addGroupMemberItems.get(getAdapterPosition()).isAdded()){
                            addGroupMemberItems.get(getAdapterPosition()).setAdded(false);
                        }else{
                            addGroupMemberItems.get(getAdapterPosition()).setAdded(true);
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
