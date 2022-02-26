package com.example.getpet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.getpet.Model.DbModel;
import com.example.getpet.Model.Pets;
import com.example.getpet.Model.User;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;


public class getDetails_Fragment extends Fragment implements View.OnClickListener {
    ImageButton back ,toProfile, editProduct;
    View view;
    Button call;
    Pets pet;
    ProgressBar progressBar;
    ImageView petImg;
    TextView typeText, nameText, areaText, ageText, phoneText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pet = getDetails_FragmentArgs.fromBundle(getArguments()).getPet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_get_details_, container, false);
        SharedPreferences sp = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        String curUSerId = sp.getString("userID", null);

        back = view.findViewById(R.id.back_btn);
        toProfile = view.findViewById(R.id.profile_btn);
        call = view.findViewById(R.id.call_btn);
        typeText = view.findViewById(R.id.get_details_type);
        nameText = view.findViewById(R.id.get_details_name);
        areaText = view.findViewById(R.id.get_details_area);
        ageText = view.findViewById(R.id.get_details_age);
        phoneText = view.findViewById(R.id.get_details_phone);
        petImg = view.findViewById(R.id.get_details_petImage);
        progressBar = view.findViewById(R.id.getDetails_progress);
        editProduct = view.findViewById(R.id.edit_btn_get_details);

        toProfile.setOnClickListener(this);
        back.setOnClickListener(this);
        call.setOnClickListener(this);

        if(curUSerId.trim().equals(pet.getOwnerId().trim())) {
            editProduct.setVisibility(View.VISIBLE);
            editProduct.setOnClickListener(this);
        }

        typeText.setText("Type: " + pet.getType());
        nameText.setText("Name: " + pet.getPetName());
        areaText.setText("Area: " + pet.getArea());
        ageText.setText("Age: " + pet.getAge());
        phoneText.setText("Phone: " + pet.getPhone());

        petImg.setImageResource(R.drawable.poodel);
        Log.d("",pet.getImg());
        if(pet.getImg() != null) {
            Picasso.get().load(pet.getImg()).into(petImg);
        }
        return view;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.profile_btn:
                Navigation.findNavController(view).navigate(getDetails_FragmentDirections.actionGetDetailsFragmentToMyProfileFragment(new User()));
                break;
            case R.id.call_btn:
                call();
                break;
            case R.id.back_btn:
                Navigation.findNavController(view).navigateUp();
                break;
            case R.id.edit_btn_get_details:
                Navigation.findNavController(view).navigate(getDetails_FragmentDirections.actionGetDetailsFragmentToEditPostFragment(pet));
                break;
            default:
                break;
        }
    }

    public void call(){
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", pet.getPhone(), null)));
        Toast.makeText(getActivity(), "phone call", Toast.LENGTH_LONG).show();
    }
}