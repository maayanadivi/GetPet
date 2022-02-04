package com.example.getpet;

import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;


public class getDetails_Fragment extends Fragment implements View.OnClickListener{
    ImageButton back ,toProfile;
    View view;
    Button call;
    String petId;
    Pets curPet;
    ProgressBar progressBar;
    ImageView petImg;
    TextView typeText, nameText, areaText, ageText, phoneText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        petId = getDetails_FragmentArgs.fromBundle(getArguments()).getPetId();
        curPet = new Pets();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_get_details_, container, false);

        back = view.findViewById(R.id.back_btn);
        toProfile = view.findViewById(R.id.profile_btn);
        call = view.findViewById(R.id.call_btn);

        petImg = view.findViewById(R.id.petImage);
        progressBar = view.findViewById(R.id.getDetails_progress);
        progressBar.setVisibility(View.VISIBLE);

        toProfile.setOnClickListener(this);
        back.setOnClickListener(this);
        call.setOnClickListener(this);

        if(petId != null) {
            DbModel.dbIns.getPet(petId, new DbModel.GetPetListener() {
                @Override
                public void onComplete(Task task, Pets pet) {
                    if(task.isSuccessful()) {
                        curPet = pet;

                        progressBar.setVisibility(View.INVISIBLE);

                        typeText.setText(curPet.getType());
                        nameText.setText(curPet.getPetName());
                        areaText.setText(curPet.getArea());
                        ageText.setText(curPet.getAge());
                        phoneText.setText(curPet.getPhone());

                        String url = curPet.getImage().toString();
                        if(!url.isEmpty()) {
                            Picasso.get().load(url).into(petImg);
                        }
                    }else{
                        // set error.
                    }
                }
            });
        }
        return view;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.profile_btn:
                Navigation.findNavController(view).navigate(getDetails_FragmentDirections.actionGetDetailsFragmentToMyProfileFragment());
                break;
            case R.id.call_btn:
                call();
                break;
            case R.id.back_btn:
                Navigation.findNavController(view).navigateUp();
                break;
        }
    }

    public void call(){
        Toast.makeText(getActivity(), "phone call", Toast.LENGTH_LONG).show();
    }
}