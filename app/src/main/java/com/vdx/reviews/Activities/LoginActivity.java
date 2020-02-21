package com.vdx.reviews.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vdx.reviews.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText Userlogin, Userpassword;
    private Button Login;


    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Toolbar mToolbar = findViewById(R.id.login_layout_Toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Sign_In);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Userlogin = findViewById(R.id.userlogin);
        Userpassword = findViewById(R.id.userpassword);
        Login = findViewById(R.id.login_button);


        mAuth = FirebaseAuth.getInstance();


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userlogin = Userlogin.getText().toString();
                String userpassword = Userpassword.getText().toString();

                signIn(userlogin, userpassword);
            }
        });


    }


    private void signIn(String userlogin, String userpass) {

        if (TextUtils.isEmpty(userlogin)) {
            Userlogin.setError("Email Required");
        }
        if (TextUtils.isEmpty(userpass)) {
            Userpassword.setError("Password Required");
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Logging You In");
            progressDialog.setMessage("Please Wait While we are Verifying");
            progressDialog.setCancelable(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(userlogin, userpass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error! Please Check Email and " +
                                        "Password Again", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }


}
