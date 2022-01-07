package com.example.getpet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class myProfile_Fragment extends Fragment implements View.OnClickListener{
    ImageButton addPost, back;
    View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_profile_, container, false);

        addPost = view.findViewById(R.id.addPost_btn);
        back = view.findViewById(R.id.back_btn);

        addPost.setOnClickListener(this);
        back.setOnClickListener(this);
        return view;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.addPost_btn:
                Navigation.findNavController(view).navigate(myProfile_FragmentDirections.actionMyProfileFragmentToAddPostFragment());
                break;
            case R.id.back_btn:
                Navigation.findNavController(view).navigateUp();
                break;
        }
    }
}