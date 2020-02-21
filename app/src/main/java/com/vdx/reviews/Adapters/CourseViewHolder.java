package com.vdx.reviews.Adapters;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdx.reviews.R;

public class CourseViewHolder extends RecyclerView.ViewHolder {

    public TextView mentor, course;
    public ImageView course_image;

    public CourseViewHolder(@NonNull View itemView) {
        super(itemView);
        mentor = itemView.findViewById(R.id.mentor);
        course = itemView.findViewById(R.id.course_name);
        course_image = itemView.findViewById(R.id.course_image);

    }
}
