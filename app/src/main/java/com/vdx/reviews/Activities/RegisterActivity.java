package com.vdx.reviews.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vdx.reviews.R;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    private EditText password, passwordc, mob_num;
    private AutoCompleteTextView Username, Email;
    private ImageButton Register;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private Button SignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar mToolbar = findViewById(R.id.register_layout_Toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Username = findViewById(R.id.username);
        Email = findViewById(R.id.email);

        password = findViewById(R.id.password);
        passwordc = findViewById(R.id.passwordc);
        Register = findViewById(R.id.register_button);
        mob_num = findViewById(R.id.mob_num);
        SignIn = findViewById(R.id.sign_in);
        Register();
        setSignIn();
    }

    private void Register() {
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(RegisterActivity.this);

                hideKeyboard();

                final String name = Username.getText().toString();
                String email = Email.getText().toString();
                String pass = password.getText().toString();
                String passc = passwordc.getText().toString();
                String mob = mob_num.getText().toString();


                AccountReg(name, email, pass, passc, mob);
            }
        });


    }

    private void setSignIn() {
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void AccountReg(String name, String email, String pass, String passc, String mob) {

        Email.setError(null);
        Username.setError(null);
        password.setError(null);
        passwordc.setError(null);
        mob_num.setError(null);


        if (TextUtils.isEmpty(name)) {

            Username.setError("User Name Not Written");

        } else if (!isValidUser(name)) {
            Username.setError("Username is too short");
        } else if (TextUtils.isEmpty(email)) {
            Email.setError("Email not Written");
        } else if (!isValidEmail(email)) {
            Email.setError("Email Not Valid");
        } else if (TextUtils.isEmpty(mob)) {
            mob_num.setError("Number not added");
        } else if (!isValidNumber(mob)) {
            mob_num.setError("Number not valid");
        } else if (TextUtils.isEmpty(pass)) {
            password.setError(getString(R.string.Password_Requiored));
        } else if (isValidPassword(pass)) {
            password.setError(getString(R.string.error_invalid_password));
        } else if (TextUtils.isEmpty(passc) && isValidPassword(passc)) {
            passwordc.setError(getString(R.string.Password_Requiored));
        } else if (isValidPassword(passc)) {
            passwordc.setError(getString(R.string.error_invalid_password));
        } else if (!Objects.equals(passc, pass)) {
            setSnackbar();
        } else {
            progressDialog.setTitle("Registering");
            progressDialog.setMessage("It may take few Seconds");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Registering(email, pass, name, mob);

        }
    }


    private void setSnackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.parent_layout), "Please check the password", Snackbar.LENGTH_LONG);
        View V = snackbar.getView();
        TextView snackview = V.findViewById(com.google.android.material.R.id.snackbar_text);
        snackview.setTextColor(Color.WHITE);
        snackview.setPadding(300, 0, 0, 0);
        V.setBackgroundResource(R.color.text_dark);
        snackbar.show();
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    private boolean isValidNumber(String email) {
        String EMAIL_PATTERN = "^[7-9][0-9]{9}$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    private boolean isValidPassword(String password) {
        return password.length() < 8;
    }

    private boolean isValidUser(String name) {
        return name.length() >= 5;
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    private void Registering(final String email, final String pass, final String name, final String mob) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            String current_user = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(current_user);
                            databaseReference.child("user_profile_name").setValue(name);
                            databaseReference.child("user_email").setValue(email);
                            databaseReference.child("user_Image").setValue("default_image");
                            databaseReference.child("user_thumb_image").setValue("default_Image");
                            databaseReference.child("mobile_number").setValue(mob)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });

                        } else {

                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error ! Please Try Again", Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }
}
