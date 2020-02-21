package com.vdx.reviews.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vdx.reviews.Activities.CourseActivity;
import com.vdx.reviews.Adapters.CourseViewHolder;
import com.vdx.reviews.Models.CourseModel;
import com.vdx.reviews.R;

import java.util.Objects;


public class CourseFragment extends Fragment {

    private RecyclerView recyclerView;

    private FirebaseRecyclerAdapter<CourseModel, CourseViewHolder> adapter;
    private ProgressDialog progressDialog;
    public static final String TAG = "FRAGMENT_COURSE";

    public CourseFragment() {
    }


    public static CourseFragment newInstance() {
        return new CourseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course, container, false);
        recyclerView = view.findViewById(R.id.fragment_course_list);
        setRecyclerView();
        return view;
    }

    private void setRecyclerView() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Course");
        databaseReference.keepSynced(true);
        progressDialog = new ProgressDialog(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<CourseModel> options =
                new FirebaseRecyclerOptions.Builder<CourseModel>()
                        .setQuery(databaseReference, CourseModel.class)
                        .setLifecycleOwner(this)
                        .build();


        adapter = new FirebaseRecyclerAdapter<CourseModel, CourseViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CourseViewHolder courseViewHolder, final int i, @NonNull final CourseModel courseModel) {
                progressDialog.dismiss();
                String mentor = "Mentor: " + courseModel.getMentor();
                final String course = "Course: " + courseModel.getCourseName();

                courseViewHolder.mentor.setText(mentor);
                courseViewHolder.course.setText(course);

                Glide.with(Objects.requireNonNull(getActivity())).load(courseModel.getUrl()).into(courseViewHolder.course_image);
                courseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CourseActivity.class);
                        intent.putExtra("position", "out");
                        intent.putExtra("course", courseModel.getCourseName());
                        intent.putExtra("mentor", courseModel.getMentor());
                        intent.putExtra("url", courseModel.getUrl());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CourseViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.course_layout, parent, false));
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
