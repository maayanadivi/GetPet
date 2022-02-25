package com.example.getpet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.getpet.Model.interfaces.GetUserById;
import com.example.getpet.Model.DbModel;
import com.example.getpet.Model.Model;
import com.example.getpet.Model.Pets;
import com.example.getpet.Model.Recycler.MyAdapter;
import com.example.getpet.Model.User;
import com.example.getpet.Model.interfaces.GetUserById;
import com.example.getpet.Model.interfaces.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class homepage_Fragment extends Fragment implements View.OnClickListener{
    homepage_FragmentViewModel viewModel;
    View view;
    SwipeRefreshLayout swipeRefresh;
    MyAdapter adapter;
    ProgressBar progressBar;
    RecyclerView list;
    ImageButton addPost, toProfile;
    User curUser;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(homepage_FragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_homepage_, container, false);
        list = view.findViewById(R.id.petlist_list_rv);
        progressBar = view.findViewById(R.id.homepage_progress);
        swipeRefresh = view.findViewById(R.id.petlist_swipe_refresh);
        addPost = view.findViewById((R.id.addPost_btn));
        toProfile = view.findViewById(R.id.profile_btn);

        SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String userId = sp.getString("userID", null);
        if(userId!=null){
            Model.instance.getUserById(userId, new GetUserById() {
                @Override
                public void onComplete(User user) {
                    curUser = user;
                }
            });
        }

        addPost.setOnClickListener(this);
        toProfile.setOnClickListener(this);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                Model.instance.reloadPetList();
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), linearLayoutManager.getOrientation());
        adapter = new MyAdapter();

        list.setHasFixedSize(true);
        list.setAdapter(adapter);
        list.setLayoutManager(linearLayoutManager);
        list.addItemDecoration(dividerItemDecoration);

        setHasOptionsMenu(true);

        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Pets>>() {
            @Override
            public void onChanged(List<Pets> posts) {
                adapter.setFragment(homepage_Fragment.this);
                adapter.setData(posts);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });

        progressBar.setVisibility(View.VISIBLE);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Pets pet = viewModel.getData().getValue().get(position);
                homepage_FragmentDirections.ActionHomepageFragmentToGetDetailsFragment action =
                        homepage_FragmentDirections.actionHomepageFragmentToGetDetailsFragment(pet);
                Navigation.findNavController(v).navigate(action);
            }
        });

        swipeRefresh.setRefreshing(Model.instance.getPetsLoadingState().getValue() == Model.LoadingState.loading);

        Model.instance.getPetsLoadingState().observe(getViewLifecycleOwner(), loadingState -> {
            swipeRefresh.setRefreshing(loadingState == Model.LoadingState.loading);
        });
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                }
                return false;
            }
        });
        return view;

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.addPost_btn:
                Log.d("button","232342356");
                Navigation.findNavController(view).navigate(homepage_FragmentDirections.actionHomepageFragmentToAddPostFragment());
                break;
            case R.id.profile_btn:
                Navigation.findNavController(view).navigate(homepage_FragmentDirections.actionHomepageFragmentToMyProfileFragment(curUser));
                break;
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pet_list ,menu);
    }


}