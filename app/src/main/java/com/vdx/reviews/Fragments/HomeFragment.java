package com.vdx.reviews.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.vdx.reviews.Activities.MainActivity;
import com.vdx.reviews.Activities.RegisterActivity;
import com.vdx.reviews.R;

import java.util.Objects;

public class HomeFragment extends Fragment {
    private AutoCompleteTextView Userlogin, Userpassword;
    private ImageButton Login;
    private Button Signup;
    private FirebaseAuth mAuth;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String userlogin = Userlogin.getText().toString();
                String userpassword = Userpassword.getText().toString();

                signIn(userlogin, userpassword);
            }
        });

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RegisterActivity.class));
            }
        });
    }

    private void initView(View view) {
        Userlogin = view.findViewById(R.id.userlogin);
        Userpassword = view.findViewById(R.id.userpassword);
        Login = view.findViewById(R.id.login_button);
        Signup = view.findViewById(R.id.sign_up);
    }


    private void signIn(String userlogin, String userpass) {

        if (TextUtils.isEmpty(userlogin)) {
            Userlogin.setError("Email Required");
        } else if (TextUtils.isEmpty(userpass)) {
            Userpassword.setError("Password Required");
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Logging You In");
            progressDialog.setMessage("Please Wait While we are Verifying");
            progressDialog.setCancelable(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(userlogin, userpass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Intent i = new Intent(getActivity(), MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            } else {
                                Toast.makeText(getActivity(), "Error! Please Check Email and " +
                                        "Password Again", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View focusedView = getActivity().getCurrentFocus();
        if (focusedView != null) {
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
