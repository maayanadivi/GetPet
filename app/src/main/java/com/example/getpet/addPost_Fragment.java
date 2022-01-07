package com.example.getpet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class addPost_Fragment extends Fragment implements View.OnClickListener{
    Button cancel, save;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_post_, container, false);

        cancel = view.findViewById(R.id.cancel_btn);
        save = view.findViewById(R.id.save_btn);

        cancel.setOnClickListener(this);
        save.setOnClickListener(this);

        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                Navigation.findNavController(view).navigateUp();
                break;
            case R.id.save_btn:
                save();
                Navigation.findNavController(view).navigateUp();
                break;
        }
    }

    private void save() {

    }
}