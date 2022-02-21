package com.example.getpet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.getpet.Model.Recycler.MyAdapter;
import com.example.getpet.Model.User;
import com.squareup.picasso.Picasso;


public class myProfile_Fragment extends Fragment implements View.OnClickListener{
    ImageButton addPost, back;
    View view;
    EditText FullName;
    User user;
    MyAdapter adapter;
    ProgressBar progressbar;
    SwipeRefreshLayout swipeRefresh;
    myProfile_FragmentViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel =  new ViewModelProvider(this).get(myProfile_FragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_profile_, container, false);
        user = myProfile_FragmentArgs.fromBundle(getArguments()).getUser();
        viewModel.setData(user);

        addPost = view.findViewById(R.id.addPost_btn);
        back = view.findViewById(R.id.back_btn);
        FullName = view.findViewById(R.id.name);
        progressbar=view.findViewById(R.id.profile_progress);
        swipeRefresh=view.findViewById(R.id.petlist_swipe_refresh);

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


    private void updateUserProfile() {
        FullName.setText(user.getFullName());
        progressbar.setVisibility(View.GONE);

    }

}