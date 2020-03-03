package com.vdx.reviews.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdx.reviews.Adapters.CommentViewHolder;
import com.vdx.reviews.Adapters.CourseViewHolder;
import com.vdx.reviews.Models.CommentModel;
import com.vdx.reviews.Models.CourseModel;
import com.vdx.reviews.R;
import com.vdx.reviews.Utils.BottomSheetDialog;
import com.vdx.reviews.Utils.Constants;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements BottomSheetDialog.dialogOnClickListener {

    private FirebaseAuth auth;
    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<CourseModel, CourseViewHolder> adapter;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    public static final String TAG = "Main";
    private CircleImageView toolbar_icon;
    BottomSheetDialog myBottomSheetDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.course_list);
        toolbar_icon = findViewById(R.id.toolbar_icon);
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Courses");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference("Course");
        databaseReference.keepSynced(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching Course");
        progressDialog.setMessage("It may take few Seconds");
        progressDialog.setCancelable(false);
        progressDialog.show();
        getUserInfo();
        toolbarIconClick();
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

                Glide.with(getApplicationContext()).load(courseModel.getUrl()).into(courseViewHolder.course_image);
                courseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), CourseActivity.class);
                        intent.putExtra("position", "in");
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
                return new CourseViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.course_layout, parent, false));
            }
        };
        recyclerView.setAdapter(adapter);
    }


    private void setUpRecycler() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child("Python");
        databaseReference.keepSynced(true);
        FirebaseRecyclerOptions<CommentModel> options =
                new FirebaseRecyclerOptions.Builder<CommentModel>()
                        .setQuery(databaseReference, CommentModel.class)
                        .build();


        FirebaseRecyclerAdapter<CommentModel, CommentViewHolder> adapters = new FirebaseRecyclerAdapter<CommentModel, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i, @NonNull CommentModel commentModel) {
                progressDialog.dismiss();
                commentViewHolder.user_rating.setRating(commentModel.getRating());
                commentViewHolder.user_review.setText(commentModel.getComment());
                commentViewHolder.user_name.setText(commentModel.getName());
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CommentViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.comments_layout, parent, false));
            }
        };
        adapters.startListening();


    }

    private void getUserInfo() {
        if (auth.getUid() != null) {
            String current_user = auth.getUid();
            final DatabaseReference user = FirebaseDatabase.getInstance().getReference("Users").child(current_user);

            user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Log.e("Main", "onDataChange: " + dataSnapshot.child("user_profile_name").getValue());
                        Constants.name = Objects.requireNonNull(dataSnapshot.child("user_profile_name").getValue()).toString();
                        Constants.number = Objects.requireNonNull(dataSnapshot.child("mobile_number").getValue()).toString();
                        Constants.email = Objects.requireNonNull(dataSnapshot.child("user_email").getValue()).toString();
                        Constants.image = Objects.requireNonNull(dataSnapshot.child("user_Image").getValue()).toString();
                        setUserIcon();
                    } catch (Exception e) {
                        Log.e(TAG, "onDataChange: ", e);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setUserIcon() {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.user_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .dontAnimate()
                .dontTransform();
        Glide.with(MainActivity.this).load(Constants.image).apply(options).into(toolbar_icon);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void bottomProfile() {
        myBottomSheetDialog = BottomSheetDialog.getInstance(this);
        Objects.requireNonNull(myBottomSheetDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myBottomSheetDialog.setOnClickListener(this);
        myBottomSheetDialog.setProfileName(Constants.name);
        myBottomSheetDialog.setProfileEmail(Constants.email);
        myBottomSheetDialog.setProfileIcon(Constants.image);
        myBottomSheetDialog.show();
    }

    private void toolbarIconClick() {
        toolbar_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomProfile();
            }
        });
    }

    @Override
    public void setdialogOnClickListener(View view) {
        switch (view.getId()) {
            case R.id.logout:
                myBottomSheetDialog.dismiss();
                auth.signOut();
                Toast.makeText(this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                Logout();
                break;
            case R.id.profile_icon:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                myBottomSheetDialog.dismiss();
        }
    }


    private void Logout() {
        Intent start = new Intent(MainActivity.this, startActivity.class);
        start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(start);
        finish();
    }
}
