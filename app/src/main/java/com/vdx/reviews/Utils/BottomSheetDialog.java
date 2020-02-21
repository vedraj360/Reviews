package com.vdx.reviews.Utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vdx.reviews.Activities.MainActivity;
import com.vdx.reviews.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetDialog extends com.google.android.material.bottomsheet.BottomSheetDialog implements View.OnClickListener {

    private CircleImageView user_icon;
    private TextView user_name, settings, logout;
    private TextView user_email;
    private Context context;
    private static BottomSheetDialog instance;
    private dialogOnClickListener dialogOnClickListener;

    public BottomSheetDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        create();
    }

    public void setOnClickListener(dialogOnClickListener dialogOnClickListener) {
        this.dialogOnClickListener = dialogOnClickListener;
    }

    public static BottomSheetDialog getInstance(@NonNull Context context) {
        return instance == null ? new BottomSheetDialog(context) : instance;
    }

    public BottomSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
    }

    protected BottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public void create() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        setContentView(bottomSheetView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // do something
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // do something
            }
        };

        user_icon = bottomSheetView.findViewById(R.id.profile_icon);
        user_email = bottomSheetView.findViewById(R.id.profile_email);
        user_name = bottomSheetView.findViewById(R.id.profile_name);
        logout = bottomSheetView.findViewById(R.id.logout);
        settings = bottomSheetView.findViewById(R.id.settings);

        user_icon.setOnClickListener(this);
        user_name.setOnClickListener(this);
        user_email.setOnClickListener(this);
        logout.setOnClickListener(this);
        settings.setOnClickListener(this);
    }

    public void setProfileIcon(String url) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.profile_user_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .dontAnimate()
                .dontTransform();
        Glide.with(context).load(url).apply(options).into(this.user_icon);
    }

    public void setProfileName(String name) {
        this.user_name.setText(name);
    }

    public void setProfileEmail(String email) {
        this.user_email.setText(email);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
            case R.id.profile_icon:
                if (dialogOnClickListener != null) {
                    dialogOnClickListener.setdialogOnClickListener(v);
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.settings:
                Toast.makeText(context, "In Development", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public interface dialogOnClickListener {
        void setdialogOnClickListener(View view);
    }
}
