package com.example.getpet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.getpet.Model.Model;
import com.example.getpet.Model.Pets;
import com.example.getpet.Model.Recycler.MyAdapter;
import com.example.getpet.Model.User;
import com.example.getpet.Model.interfaces.OnItemClickListener;


import java.util.List;


public class myProfile_Fragment extends Fragment implements View.OnClickListener{
    ImageButton addPost, back;
    View view;
    User user;
    MyAdapter adapter;
    ProgressBar progressbar;
    SwipeRefreshLayout swipeRefresh;
    myProfile_FragmentViewModel viewModel;
    Button logout;
    RecyclerView recyclerView;
    ProgressBar progressBar;

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

        addPost = view.findViewById(R.id.addPost_btn);
        back = view.findViewById(R.id.back_btn);
        swipeRefresh = view.findViewById(R.id.profile_swipe_refresh);
        recyclerView = view.findViewById(R.id.recycler_profile);
        logout = view.findViewById(R.id.logout_btn);
        progressbar = view.findViewById(R.id.profile_progress);

        progressbar.setVisibility(View.VISIBLE);

        addPost.setOnClickListener(this);
        back.setOnClickListener(this);
        logout.setOnClickListener(this);

        viewModel.setData(user.getId());

        adapter = new MyAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        viewModel.getData().observe(getViewLifecycleOwner(), posts -> {
            adapter.setFragment(myProfile_Fragment.this);
            adapter.setData(posts);
            adapter.notifyDataSetChanged();
            progressbar.setVisibility(View.INVISIBLE);
        });

        adapter.setOnItemClickListener((position, v) -> {
            Pets pet = viewModel.getData().getValue().get(position);
            myProfile_FragmentDirections.ActionMyProfileFragmentToGetDetailsFragment action =
                    myProfile_FragmentDirections.actionMyProfileFragmentToGetDetailsFragment(pet);
            Navigation.findNavController(v).navigate(action);
        });

        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(true);
            Model.instance.reloadPetList();
            adapter.notifyDataSetChanged();
            swipeRefresh.setRefreshing(false);
        });

        swipeRefresh.setRefreshing(Model.instance.getPetsLoadingState().getValue() == Model.LoadingState.loading);

        Model.instance.getPetsLoadingState().observe(getViewLifecycleOwner(), loadingState -> {
            swipeRefresh.setRefreshing(loadingState == Model.LoadingState.loading);
        });

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

            case R.id.logout_btn:
                SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
                SharedPreferences.Editor Ed=sp.edit();
                Ed.remove("email");
                Ed.remove("password");
                Ed.remove("userId");
                Ed.commit();

                Navigation.findNavController(view).navigate(myProfile_FragmentDirections.actionMyProfileFragmentToLoginFragment());

        }
    }

}