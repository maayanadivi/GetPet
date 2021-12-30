package com.example.getpet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class loginFragment extends Fragment implements View.OnClickListener {
    EditText email, password, name;
    Button loginBtn, signupBtn;
    private FirebaseAuth mAuth;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        loginBtn = view.findViewById(R.id.button_login);
        signupBtn = view.findViewById(R.id.singUp_btn);

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
            case R.id.singUp_btn:
                Navigation.findNavController(v).navigate(loginFragmentDirections.actionLoginFragmentToSignupFragment());
                break;
        }
    }

    private void signin() {

    }
}