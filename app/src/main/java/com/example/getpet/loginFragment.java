package com.example.getpet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.getpet.Model.DbModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class loginFragment extends Fragment implements View.OnClickListener {
    EditText email, password;
    Button loginBtn, signupBtn;
    View view;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);

        loginBtn = view.findViewById(R.id.button_login);
        signupBtn = view.findViewById(R.id.signUp_btn);

        progressBar = view.findViewById(R.id.login_progress);

        loginBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_login:
                signin();
                break;
            case R.id.signUp_btn:
                Log.d("1", "asdas");
                Navigation.findNavController(view).navigate(loginFragmentDirections.actionLoginFragmentToSignupFragment());
                break;
        }
    }

    private void signin() {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if(userEmail.isEmpty()) {
            email.setError("Required Field");
            email.requestFocus();
            return;
        }
        if(userPassword.isEmpty()) {
            password.setError("Required Field");
            email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Email not valid");
            email.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        DbModel.dbIns.loginUser(userEmail, userPassword, new DbModel.LoginUserListener() {
            @Override
            public void onComplete(FirebaseUser user, Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Navigation.findNavController(view).navigate(loginFragmentDirections.actionLoginFragmentToHomepageFragment());
                }else {
                    Toast.makeText(getActivity(), "Login Failed, email/password is not valid.", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}