package com.vdx.reviews.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vdx.reviews.R;
import com.vdx.reviews.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class AddReviewActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialRatingBar ratingBar;


    private TextInputEditText review;
    private String textReview;
    private String course;
    private String day, edit;
    private boolean Empty, rateEmpty = true;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private TextView post;
    private Button back;
    private int ratings;
    private int current_rating, prev_rating;
    private float rate;
    public static final String TAG = "ADD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        initViews();
        course = getIntent().getStringExtra("course");
        edit = getIntent().getStringExtra("edit");


        assert edit != null;
        if (edit.equals("edit")) {
            editFunction();
        } else {
            setRating();
            setCommentRatings();
        }
        tool();
        onBackClick();

        onPostClick();

    }

    private void initViews() {
        toolbar = findViewById(R.id.create_toolbar);
        back = findViewById(R.id.back);
        post = findViewById(R.id.post);
        ratingBar = findViewById(R.id.review_rating);
        review = findViewById(R.id.add_review);
        progressDialog = new ProgressDialog(this);
    }

    private void setRating() {
        rate = getIntent().getFloatExtra("rate", 0);
        ratingBar.setRating(rate);

    }

    private void setCommentRatings() {
        ratings = (int) (getIntent().getIntExtra("ratings", 0) + rate);
    }

    private void setProgressDialog() {
        progressDialog.setTitle("Posting");
        progressDialog.setMessage("It may take few Seconds");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    private void checkEditText() {

        if (review.length() != 0) {
            textReview = String.valueOf(review.getText());
            Empty = false;
        } else {
            review.setError("Empty");
            Empty = true;
        }

        float r = ratingBar.getRating();
        rateEmpty = !(r > 0);

    }

    private void postReview() {
        setDateTime();
        if (edit.equals("edit")) {
            rateAdd();
        }
        String current_user = FirebaseAuth.getInstance().getUid();
        assert current_user != null;

        DatabaseReference course_rating = FirebaseDatabase.getInstance().getReference("Ratings").child(course);
        course_rating.child("total_ratings").setValue(ratings);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(course).child(current_user);
        databaseReference.child("comment").setValue(textReview);
        databaseReference.child("name").setValue(Constants.name);
        databaseReference.child("day").setValue(day);
        databaseReference.child("number").setValue(Constants.number);
        databaseReference.child("rating").setValue(ratingBar.getRating()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    finish();
                    progressDialog.dismiss();
                } else {
                    Toast.makeText(AddReviewActivity.this, "Error while posting", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

    }

    private void onPostClick() {
        post.setOnClickListener(this);
    }

    private void onBackClick() {
        back.setOnClickListener(this);
    }

    private void tool() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    private void setDateTime() {

        day = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            checkEditText();
            if (!Empty) {
                backDialog();
            } else {
                finish();
            }

        } else if (v.getId() == R.id.post) {
            checkEditText();
            if (!Empty) {
                if (!rateEmpty) {
                    setProgressDialog();
                    postReview();
                } else {
                    Toast.makeText(this, "Rating required", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Review Required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void backDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.back_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button keep = dialog.findViewById(R.id.keep_writing);
        Button discard = dialog.findViewById(R.id.discard);

        keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }

        });
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();

            }
        });
        dialog.show();
    }

    private void editFunction() {
        rate = getIntent().getIntExtra("rate", 0);
        ratingBar.setRating(rate);
        review.setText(getIntent().getStringExtra("comment"));

    }

    private void rateAdd() {
        prev_rating = (int) rate;
        current_rating = (int) ratingBar.getRating();
        ratings = getIntent().getIntExtra("ratings", 0);
        Log.e(TAG, "editFunction: " + prev_rating + " " + current_rating);
        if (current_rating < prev_rating) {
            ratings = ratings + current_rating - prev_rating;
            Log.e(TAG, "editFunction: " + ratings);
        } else if (current_rating > prev_rating) {
            Log.e(TAG, "editFunction: " + ratings);
            ratings = ratings + current_rating - prev_rating;
        }
    }

}
