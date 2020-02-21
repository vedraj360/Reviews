package com.vdx.reviews.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vdx.reviews.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private final static int Gallery_pic = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private StorageReference thumbstorageRef;
    private Bitmap thumb_bitmap = null;
    private ProgressDialog progressDialog;
    private TextView profile_name, profile_email, profile_number, pro_name, profile_back;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = new Intent(this, MainActivity.class);
        initViews();

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getUid()));
        Log.e("TAG", "onCreate: " + databaseReference);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile_Images");
        thumbstorageRef = FirebaseStorage.getInstance().getReference().child("Thumb_Images");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String user_name = dataSnapshot.child("user_profile_name").getValue().toString();
                String user_email = dataSnapshot.child("user_email").getValue().toString();
                String user_Image = dataSnapshot.child("user_Image").getValue().toString();
                String user_number = dataSnapshot.child("mobile_number").getValue().toString();
                String s = "@" + user_name;
                profile_name.setText(user_name);
                profile_email.setText(user_email);
                profile_number.setText(user_number);
                pro_name.setText(s);
                if (!user_Image.equals("default_Image")) {
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.user)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH)
                            .dontAnimate()
                            .dontTransform();
                    Glide.with(getApplicationContext()).load(user_Image).apply(options).into(profile_image);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageOnClick();
        OnbackCilick();
    }

    private void initViews() {
        profile_image = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.Profile_Activity_Name);
        profile_email = findViewById(R.id.Profile_Activity_Email);
        profile_number = findViewById(R.id.Profile_Activity_Number);
        pro_name = findViewById(R.id.pro_name);
        profile_back = findViewById(R.id.profile_back);

    }

    private void imageOnClick() {
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryImage = new Intent();
                galleryImage.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimeTypes = {"image/jpeg", "image/png"};
                galleryImage.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                galleryImage.setType("image/*");
                ProfileActivity.this.startActivityForResult(galleryImage, Gallery_pic);
            }
        });
    }

    private void OnbackCilick() {
        profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addUrl(String url) {
        databaseReference.child("user_Image").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile Image");
        progressDialog.setMessage("It may take few Seconds");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String TAG = "PRO";
        if (requestCode == Gallery_pic && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            profile_image.setImageURI(selectedImage);
            CropImage.activity(selectedImage)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                setProgressDialog();
                Uri resultUri = result.getUri();
                File thumb_File_Uri = new File(Objects.requireNonNull(resultUri.getPath()));
                String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                try {

                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(60)
                            .compressToBitmap(thumb_File_Uri);
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);


                    final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                    final StorageReference imagepath = storageReference.child(user_id + ".jpg");
                    final StorageReference thumb_File_Path = thumbstorageRef.child(user_id + ".jpg");
                    UploadTask uploadTask = imagepath.putFile(resultUri);

                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return imagepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                Map<String, Object> update_user_data = new HashMap<>();
                                update_user_data.put("user_Image", downloadUri);
                                Log.e(TAG, "onComplete: " + update_user_data);
//                                update_user_data.put("user_thumb_image", thumb_File_Path.getDownloadUrl());

                                try {
                                    assert downloadUri != null;
                                    addUrl(downloadUri.toString());
                                } catch (Exception e) {
                                    Log.e(TAG, "onComplete: ", e);
                                }

                            } else {
                                Toast.makeText(ProfileActivity.this.getApplicationContext(), "Something Went Wrong! Please Try Again", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else {

            Log.e(TAG, "onActivityResult: " + "else");
        }

    }

}


