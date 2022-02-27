package com.example.getpet.Model.Recycler;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getpet.Model.Pets;
import com.example.getpet.Model.interfaces.OnItemClickListener;
import com.example.getpet.R;
import com.example.getpet.homepage_Fragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    OnItemClickListener listener;
    private List<Pets> data;
    private Fragment fragment;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = fragment.getLayoutInflater().inflate(R.layout.fragment_pet__list__row,parent,false);
        MyViewHolder holder = new MyViewHolder(view,listener);
        return holder;
    }

    public void setData(List <Pets> data) {
        this.data = data;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public int getItemCount() {
        if(data==null)
            return 0;
        return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pets p = data.get(position);
        holder.area.setText("Area: " + p.getArea());
        holder.name.setText("Name: " + p.getPetName());
        holder.type.setText(p.getType());

        if (p.getImg() != null) {
            Picasso.get().load(p.getImg()).into(holder.img);
        }
    }
}
