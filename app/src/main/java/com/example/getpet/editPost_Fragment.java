package com.example.getpet;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.getpet.Model.Model;
import com.example.getpet.Model.Pets;
import com.example.getpet.Model.interfaces.DeletePetsListener;
import com.example.getpet.Model.interfaces.EditPetsListener;
import com.squareup.picasso.Picasso;

public class editPost_Fragment extends Fragment implements View.OnClickListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Button save, cancel, delete;
    View view;
    EditText type, name, age, contact, area;
    ImageButton takeImage;
    Pets pet;
    ProgressBar progressBar;
    Bitmap bitmap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pet = editPost_FragmentArgs.fromBundle(getArguments()).getPet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_post_, container, false);

        type = view.findViewById(R.id.type);
        name = view.findViewById(R.id.Name);
        area = view.findViewById(R.id.area);
        age = view.findViewById(R.id.age);
        progressBar = view.findViewById(R.id.edit_pet_progress);
        contact = view.findViewById(R.id.contact);
        takeImage = view.findViewById(R.id.takeImage);
        delete = view.findViewById(R.id.delete_btn);

        type.setText(pet.getType());
        name.setText(pet.getPetName());
        area.setText(pet.getArea());
        age.setText(pet.getAge());
        contact.setText(pet.getPhone());

        Picasso.get().load(pet.getImg()).into(takeImage);

        save = view.findViewById(R.id.save_btn);
        cancel = view.findViewById(R.id.cancel_btn);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        takeImage.setOnClickListener(this);
        delete.setOnClickListener(this);

        return view;
    }

    public void onClick(View v) {
        Log.d("23","3333333");
        switch(v.getId()) {
            case R.id.save_btn:
                saveEditedPost();
                break;
            case R.id.takeImage:
                uploadImage();
                break;
            case R.id.cancel_btn:
                Navigation.findNavController(view).navigateUp();
                break;
            case R.id.delete_btn:
                deletePost();
                break;
            default:
                break;
        }
    }

    public void deletePost() {
        Model.instance.deletePet(pet, new DeletePetsListener() {
            @Override
            public void onComplete() {
                Toast.makeText(getActivity(), "Deletion succeeded.", Toast.LENGTH_LONG).show();
                Navigation.findNavController(view).navigate(editPost_FragmentDirections.actionEditPostFragmentToHomepageFragment());
            }
        });
    }

    public void saveEditedPost() {
        progressBar.setVisibility(View.VISIBLE);


        String typeInput, petNameInput, areaInput, ageInput, phoneInput;
        typeInput = type.getText().toString();
        petNameInput = name.getText().toString();
        areaInput = area.getText().toString();
        ageInput = age.getText().toString();
        phoneInput = contact.getText().toString();


        if (typeInput.isEmpty()){
            type.setError("Required Field");
            type.requestFocus();
            return; }

        if (petNameInput.isEmpty()){
            name.setError("Required Field");
            name.requestFocus();
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
            contact.setError("Required Field");
            contact.requestFocus();
            return; }

        pet.setAge(ageInput);
        pet.setType(typeInput);
        pet.setArea(areaInput);
        pet.setPetName(petNameInput);
        pet.setPhone(phoneInput);

        Model.instance.editPost(pet, bitmap, new EditPetsListener() {
            @Override
            public void onComplete(Pets pets) {
                progressBar.setVisibility(View.INVISIBLE);
                Model.instance.reloadPetList();
                Navigation.findNavController(view).navigate(editPost_FragmentDirections.actionEditPostFragmentToHomepageFragment());
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