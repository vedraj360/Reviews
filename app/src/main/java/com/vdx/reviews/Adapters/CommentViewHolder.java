package com.vdx.reviews.Adapters;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdx.reviews.R;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView user_icon;
    public TextView user_name, user_review, day;
    public MaterialRatingBar user_rating;
    public Button whats, message;

    public CommentViewHolder(@NonNull View view) {
        super(view);
        initView(view);
    }

    private void initView(View view) {
        user_name = view.findViewById(R.id.user_name);
        user_review = view.findViewById(R.id.user_comments);
        user_rating = view.findViewById(R.id.user_rating);
        day = view.findViewById(R.id.date);
        whats = view.findViewById(R.id.whats);
        message = view.findViewById(R.id.message);
    }
}
