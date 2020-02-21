package com.vdx.reviews.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.vdx.reviews.R;


public class WelcomeActivity extends AppCompatActivity {

    private static final String PREF_NAME = "welcome";

    static final String FIRST_LAUNCH = "FIRST_LAUNCH";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefshare prefshare = new prefshare(this);
        if (prefshare.First()) {
            prefshare.setFirst(false);
            splash();
        } else {
            Intent i = new Intent(getApplicationContext(), startActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_welcome);


    }

    public void splash() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        }, 4000);
    }

    public class prefshare {
        SharedPreferences sp;
        SharedPreferences.Editor spe;
        Context context = null;
        int PRIVATE_MODE = 0;

        prefshare(Context context) {
            this.context = context;
            sp = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            spe = sp.edit();
        }

        void setFirst(boolean first) {
            spe.putBoolean(FIRST_LAUNCH, first);
            spe.commit();
        }

        boolean First() {
            return sp.getBoolean(FIRST_LAUNCH, true);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
