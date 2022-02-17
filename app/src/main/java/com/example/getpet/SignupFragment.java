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
import com.example.getpet.Model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class SignupFragment extends Fragment implements View.OnClickListener {
    EditText email, password, name;
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_signup, container, false);

        name = view.findViewById(R.id.signup_name);
        email = view.findViewById(R.id.signup_email);
        password = view.findViewById(R.id.signup_password);

        loginBtn = view.findViewById(R.id.login_btn_up);
        signupBtn = view.findViewById(R.id.signup_up);

        progressBar = view.findViewById(R.id.signup_progress);

        loginBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);

        return view;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.login_btn_up:
                Navigation.findNavController(view).navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment());
                break;
            case R.id.signup_up:
                signup();
                break;
        }
    }

    private void signup() {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String userName = name.getText().toString();

        if(userEmail.isEmpty()) {
            email.setError("Required Field");
            email.requestFocus();
            return;
        }
        if(userPassword.isEmpty()) {
            password.setError("Required Field");
            password.requestFocus();
            return;
        }
        if(userName.isEmpty()) {
            name.setError("Required Field");
            name.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email.setError("Email not valid");
            email.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        DbModel.dbIns.registerUser(new User(userEmail, userName), userPassword, (user, task) -> {
            Log.d("task", task.toString());
            if(task.isSuccessful()) {
                Toast.makeText(getActivity(), "Sign-up success.", Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                Navigation.findNavController(view).navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment());
            } else {
                Toast.makeText(getActivity(), "Sign-up Failed, email/password is not valid.", Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }
}