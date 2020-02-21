package com.vdx.reviews.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdx.reviews.Adapters.CommentViewHolder;
import com.vdx.reviews.Models.CommentModel;
import com.vdx.reviews.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Objects;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class CourseActivity extends AppCompatActivity {


    private MaterialRatingBar ratingBar, edit_user_rating, all_rating_bar;
    private TextView write, edit_review, cname, mname, edit_user, edit_comment, edit_day, tot_ratings, all_rating;

    private RecyclerView recyclerView;
    private ImageView courseImage;
    private FirebaseRecyclerAdapter<CommentModel, CommentViewHolder> adapter;
    private ProgressDialog progressDialog;
    public static final String TAG = "COURSE";
    private String COURSE, URL, MENTOR, name, comment, day;
    ;
    private int total_comments, course_ratings;
    private int rating = 0;
    private LinearLayout editLayout, rateLayout;

    //https://stackoverflow.com/questions/28147720/how-to-expand-only-textview-in-android

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        COURSE = getIntent().getStringExtra("course");
        URL = getIntent().getStringExtra("url");
        MENTOR = getIntent().getStringExtra("mentor");
        String pos = getIntent().getStringExtra("position");
        initViews();
        setData();
        onReviewClick();
        onRateClick();
        assert pos != null;
        if (pos.equals("in")) {
            checkReview();
        }
        getRating();
        onEditReviewClick();

    }

    private void initViews() {
        ratingBar = findViewById(R.id.rating);
        write = findViewById(R.id.write_review);
        recyclerView = findViewById(R.id.comment_list);
        cname = findViewById(R.id.c_name);
        mname = findViewById(R.id.m_name);
        courseImage = findViewById(R.id.c_image);
        editLayout = findViewById(R.id.edit_layout);
        rateLayout = findViewById(R.id.rate_layout);
        edit_review = findViewById(R.id.edit_review);
        edit_user_rating = findViewById(R.id.edit_user_rating);
        edit_user = findViewById(R.id.edit_user_name);
        edit_comment = findViewById(R.id.edit_user_comment);
        edit_day = findViewById(R.id.edit_date);
        tot_ratings = findViewById(R.id.tot_rating);
        all_rating_bar = findViewById(R.id.all_rating_bar);
        all_rating = findViewById(R.id.all_rating);

    }

    private void setUpRecycler() {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(COURSE);
        databaseReference.keepSynced(true);
        FirebaseRecyclerOptions<CommentModel> options =
                new FirebaseRecyclerOptions.Builder<CommentModel>()
                        .setQuery(databaseReference, CommentModel.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<CommentModel, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CommentViewHolder commentViewHolder, int i, @NonNull final CommentModel commentModel) {

                commentViewHolder.user_rating.setRating(commentModel.getRating());
                commentViewHolder.user_review.setText(commentModel.getComment());
                commentViewHolder.user_name.setText(commentModel.getName());
                commentViewHolder.day.setText(commentModel.getDay());
                commentViewHolder.whats.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        whatsDialog(commentModel.getNumber(), "whatsapp");

                    }
                });
                commentViewHolder.message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        whatsDialog(commentModel.getNumber(), "message");

                    }
                });
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CommentViewHolder(LayoutInflater.from(CourseActivity.this).inflate(R.layout.comments_layout, parent, false));
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }

    private void setData() {
        cname.setText(COURSE);
        mname.setText(MENTOR);
        Glide.with(getApplicationContext()).load(URL).into(courseImage);
    }

    private void getCommentCount() {

        DatabaseReference counts = FirebaseDatabase.getInstance().getReference("Comments").child(COURSE);

        counts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    total_comments = (int) dataSnapshot.getChildrenCount();
                    Log.e(TAG, "onDataChange: " + total_comments);
                    setTotalRating();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getRating() {

        final DatabaseReference rating = FirebaseDatabase.getInstance().getReference("Ratings").child(COURSE);

        rating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    try {
                        Object rate = Objects.requireNonNull(dataSnapshot.child("total_ratings").getValue());
                        course_ratings = Integer.parseInt(rate.toString());
                        getCommentCount();
                        Log.d(TAG, "onDataChange: " + course_ratings);
                    } catch (Exception e) {
                        Log.d(TAG, "onDataChange: ", e);
                    }
                } else {
                    course_ratings = 0;

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkReview() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user = auth.getUid();
        assert user != null;
        DatabaseReference user_comment = FirebaseDatabase.getInstance().getReference("Comments").child(COURSE).child(user);
        user_comment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    try {
                        long rate = (long) Objects.requireNonNull(dataSnapshot.child("rating").getValue());
                        rating = (int) rate;
                        name = String.valueOf(Objects.requireNonNull(dataSnapshot.child("name").getValue()));
                        day = String.valueOf(Objects.requireNonNull(dataSnapshot.child("day").getValue()));


                        Log.d(TAG, "onDataChange: " + Objects.requireNonNull(dataSnapshot.child("rating").getValue()));
                        comment = String.valueOf(Objects.requireNonNull(dataSnapshot.child("comment").getValue()));
                        setEditView();
                    } catch (Exception e) {
                        Log.e(TAG, "onDataChange: ", e);
                    }
                } else {
                    editLayout.setVisibility(View.GONE);
                    rateLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setEditView() {
        if (name != null && comment != null) {
            editLayout.setVisibility(View.VISIBLE);
            rateLayout.setVisibility(View.GONE);
            edit_comment.setText(comment);
            edit_user_rating.setRating(rating);
            edit_user.setText(name);
            edit_day.setText(day);
        } else {
            editLayout.setVisibility(View.GONE);
            rateLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Posting");
        progressDialog.setMessage("It may take few Seconds");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }


    private void onRateClick() {
        try {
            ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
                @Override
                public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                    Float rate = ratingBar.getRating();
                    Intent intent = new Intent(getApplicationContext(), AddReviewActivity.class);
                    intent.putExtra("edit", "review");
                    intent.putExtra("rate", rate);
                    intent.putExtra("course", COURSE);
                    intent.putExtra("ratings", course_ratings);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onRateClick: ", e);
        }

    }


    private void onReviewClick() {
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float rate = ratingBar.getRating();
                Intent intent = new Intent(getApplicationContext(), AddReviewActivity.class);
                intent.putExtra("edit", "review");
                intent.putExtra("course", COURSE);
                intent.putExtra("ratings", course_ratings);
                intent.putExtra("rate", rate);
                startActivity(intent);
            }
        });
    }

    private void onEditReviewClick() {
        edit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddReviewActivity.class);
                intent.putExtra("edit", "edit");
                intent.putExtra("course", COURSE);
                intent.putExtra("ratings", course_ratings);
                intent.putExtra("comment", comment);
                intent.putExtra("rate", rating);
                startActivity(intent);
            }
        });
    }

    private void setTotalRating() {
        if (total_comments == 1) {
            String t = total_comments + " rating";
            tot_ratings.setText(t);

        } else {
            String t = total_comments + " ratings";
            tot_ratings.setText(t);

        }
        float per = (float) course_ratings / (float) total_comments;
        Log.d(TAG, "setTotalRating: " + per + " " + course_ratings);
        all_rating_bar.setRating(per);
        String value = String.format(Locale.US, "%.2f", per);
        all_rating.setText(value);
    }


    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return app_installed;
    }

    private void sendWhatsMessage(String number, String message) {


        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                String url = "https://api.whatsapp.com/send?phone=" + "91" + number + "&text=" + URLEncoder.encode(message, "UTF-8");
                i.setPackage("com.whatsapp");
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "WhatsApp not Installed",
                    Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);

        }

    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }


    private void whatsDialog(final String number, final String reqType) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.whats_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button send = dialog.findViewById(R.id.whats_send);
        Button cancel = dialog.findViewById(R.id.whats_cancel);
        final TextInputEditText message = dialog.findViewById(R.id.whats_message);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mess = Objects.requireNonNull(message.getText()).toString();
                if (mess.length() != 0) {
                    if (!reqType.equals("message")) {
                        sendWhatsMessage(number, mess);
                    } else {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CourseActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                            Toast.makeText(CourseActivity.this, "Permission Required", Toast.LENGTH_SHORT).show();
                            sendSMS(number, mess);

                        } else {
                            sendSMS(number, mess);

                        }

                    }
                    dialog.dismiss();

                } else {
                    message.setError("Empty");
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        setUpRecycler();
        Log.d(TAG, "onStart:");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
        Log.d(TAG, "onStop: ");
    }


}
