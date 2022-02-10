package com.example.getpet;

import android.content.Context;
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
import android.widget.TextView;

import com.example.getpet.Model.Model;
import com.example.getpet.Model.Pets;
import com.squareup.picasso.Picasso;

import java.util.List;


public class homepage_Fragment extends Fragment implements View.OnClickListener{
    ImageButton addPost, toProfile;
    View view;
    homepage_FragmentViewModel viewModel;
    SwipeRefreshLayout swipeRefresh;
    MyAdapter adapter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(homepage_FragmentViewModel.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_homepage_, container, false);
        RecyclerView list = view.findViewById(R.id.petlist_list_rv);

        adapter = new MyAdapter();
        list.setAdapter(adapter);
        list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        list.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        setHasOptionsMenu(true);

        viewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Pets>>() {
            @Override
            public void onChanged(List<Pets> posts) {
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Pets pet = viewModel.getData().getValue().get(position);
                Log.d("TAG","row was clicked " + position);
//                homepage_FragmentDirections.ActionHomepageFragmentToGetDetailsFragment action = homepage_FragmentDirections.ActionHomepageFragmentToGetDetailsFragment(pet.getType());
//                Navigation.findNavController(v).navigate(action);
            }
        });

        swipeRefresh = view.findViewById(R.id.petlist_swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                Model.instance.reloadPetList();
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        setHasOptionsMenu(true);

        viewModel.getData().observe(getViewLifecycleOwner(), (studentsList)->{
            adapter.notifyDataSetChanged();
        });
        swipeRefresh.setRefreshing(Model.instance.getStudentListLoadingState().getValue() == Model.LoadingState.loading);
        Model.instance.getStudentListLoadingState().observe(getViewLifecycleOwner(), loadingState -> {
            swipeRefresh.setRefreshing(loadingState == Model.LoadingState.loading);
        });

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

    private void refreshData() {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pet_list ,menu);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView area, name, type;
        ImageView img;
        Button moreInfoBtn;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            area = itemView.findViewById(R.id.list_row_area);
            type = itemView.findViewById(R.id.list_row_type);
            name = itemView.findViewById(R.id.list_row_name);
            moreInfoBtn = itemView.findViewById(R.id.list_row_more);
            img = itemView.findViewById(R.id.list_row_avatar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(pos,v);
                    }
                }
            });
        }

        public void bind(Pets pet){
            area.setText(pet.getArea());
            type.setText(pet.getType());
            name.setText(pet.getPetName());
            String url = pet.getImage().toString();
            if (url != null && !url.equals("")) {
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.poodel)
                        .into(img);
            }
        }
    }

    interface OnItemClickListener{
        void onItemClick(int position, View v);
    }
    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        OnItemClickListener listener;
        public void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.fragment_homepage_,parent,false);
            MyViewHolder holder = new MyViewHolder(view,listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Pets pet = viewModel.getData().getValue().get(position);
            holder.bind(pet);
        }

        @Override
        public int getItemCount() {
            if (viewModel.getData().getValue() == null) return 0;
            return viewModel.getData().getValue().size();
        }
    }
}