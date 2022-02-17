package com.example.getpet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class editPost_Fragment extends Fragment implements View.OnClickListener{
    Button save, cancel;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_post_, container, false);

        save = view.findViewById(R.id.save_btn);
        cancel = view.findViewById(R.id.cancel_btn);

        return view;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.save_btn:
                Navigation.findNavController(view).navigate(editPost_FragmentDirections.actionEditPostFragmentToMyPostFragment());
                break;
            case R.id.cancel_btn:
                Navigation.findNavController(view).navigateUp();
                break;
        }
    }
}