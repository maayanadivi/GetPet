package com.example.getpet.Model.Recycler;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getpet.Model.Pets;
import com.example.getpet.Model.interfaces.OnItemClickListener;
import com.example.getpet.R;
import com.example.getpet.homepage_Fragment;
import com.squareup.picasso.Picasso;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView area, name, type;
    ImageView img;
    Button moreInfoBtn;

    public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
        super(itemView);
        area = itemView.findViewById(R.id.list_row_area);
        type = itemView.findViewById(R.id.list_row_type);
        name = itemView.findViewById(R.id.list_row_name);
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


}
