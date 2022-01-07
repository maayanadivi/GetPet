package com.example.getpet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


public class getDetails_Fragment extends Fragment implements View.OnClickListener{
    ImageButton back ,toProfile;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_get_details_, container, false);

        back = view.findViewById(R.id.back_btn);
        toProfile = view.findViewById(R.id.profile_btn);

        toProfile.setOnClickListener(this);
        back.setOnClickListener(this);

        return view;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.profile_btn:
                Navigation.findNavController(view).navigate(getDetails_FragmentDirections.actionGetDetailsFragmentToMyProfileFragment());
                break;
            case R.id.back_btn:
                Navigation.findNavController(view).navigateUp();
                break;
        }
    }
}