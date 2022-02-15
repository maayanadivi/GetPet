package com.example.getpet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.getpet.Model.User;

public class myPostFragment extends Fragment implements View.OnClickListener{

    ImageButton delete, edit, back;
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_post, container, false);

        delete = view.findViewById(R.id.trush_btn);
        edit = view.findViewById(R.id.edit_btn);
        back = view.findViewById(R.id.back_btn);

        delete.setOnClickListener(this);
        edit.setOnClickListener(this);
        back.setOnClickListener(this);

        return view;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.trush_btn:
                Navigation.findNavController(view).navigate(myPostFragmentDirections.actionMyPostFragmentToMyProfileFragment(new User()));
                break;
            case R.id.edit_btn:
                Navigation.findNavController(view).navigate(myPostFragmentDirections.actionMyPostFragmentToEditPostFragment());
                break;
            case R.id.back_btn:
                Navigation.findNavController(view).navigateUp();
                break;
        }
    }
}