package com.example.getpet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class homepage_Fragment extends Fragment implements View.OnClickListener{
    ImageButton addPost, toProfile;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_homepage_, container, false);

        addPost = view.findViewById(R.id.addPost_btn);
        toProfile = view.findViewById(R.id.profile_btn);

        addPost.setOnClickListener(this);
        toProfile.setOnClickListener(this);

        return view;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.addPost_btn:
                Navigation.findNavController(view).navigate(homepage_FragmentDirections.actionHomepageFragmentToAddPostFragment());
                break;
            case R.id.profile_btn:
                Navigation.findNavController(view).navigate(homepage_FragmentDirections.actionHomepageFragmentToMyProfileFragment());
                break;
        }
    }
}