package com.example.getpet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;
import com.example.getpet.Model.DbModel;
import com.example.getpet.Model.Pets;
import com.example.getpet.Model.interfaces.UploadPetListener;
import com.google.android.gms.tasks.Task;



public class addPost_Fragment extends Fragment implements View.OnClickListener{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Button cancel, save;
    ImageButton takeImage;
    View view;
    EditText type, petName, area, age, phone;
    Bitmap bitmap;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_post_, container, false);

        cancel = view.findViewById(R.id.cancel_btn);
        save = view.findViewById(R.id.save_btn);

        takeImage = view.findViewById(R.id.takeImage);

        type = view.findViewById(R.id.type);
        petName = view.findViewById(R.id.Name);
        area = view.findViewById(R.id.area);
        age = view.findViewById(R.id.age);
        phone = view.findViewById(R.id.contact);


        progressBar = view.findViewById(R.id.addPost_progress);

        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        takeImage.setOnClickListener(this);

        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                Navigation.findNavController(view).navigateUp();
                break;
            case R.id.takeImage:
                uploadImage();
                break;
            case R.id.save_btn:
                save();
                break;
        }
    }

    private void save() {

       String typeInput, petNameInput, areaInput, ageInput, phoneInput;
       typeInput = type.getText().toString();
       petNameInput = petName.getText().toString();
       areaInput = area.getText().toString();
       ageInput = age.getText().toString();
       phoneInput = phone.getText().toString();


       if (typeInput.isEmpty()){
           type.setError("Required Field");
           type.requestFocus();
           return; }

       if (petNameInput.isEmpty()){
           petName.setError("Required Field");
           petName.requestFocus();
           return; }

       if (areaInput.isEmpty()){
           area.setError("Required Field");
           area.requestFocus();
           return; }

       if (ageInput.isEmpty()){
           age.setError("Required Field");
           age.requestFocus();
           return; }

       if (phoneInput.isEmpty()){
           phone.setError("Required Field");
           phone.requestFocus();
           return; }

       if (bitmap == null){ return;}

       progressBar.setVisibility(View.VISIBLE);

       Pets pet = new Pets(typeInput,petNameInput,areaInput,ageInput,phoneInput,"" );

       DbModel.dbIns.uploadPet(pet, bitmap, new UploadPetListener() {
           @Override
           public void onComplete(Task task, Pets pet) {
               if(pet.getImg() != null) {
                   Toast.makeText(getActivity(), "Upload success.", Toast.LENGTH_LONG).show();
                   Navigation.findNavController(view).navigateUp();
               } else {
                   Toast.makeText(getActivity(), "Upload Failed.", Toast.LENGTH_LONG).show();
               }
               progressBar.setVisibility(View.GONE);
           }
       });
    }

    private void uploadImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            takeImage.setImageBitmap(bitmap);
        }
    }
}