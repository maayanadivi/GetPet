package com.example.getpet.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class DbModel {
    public static final DbModel dbIns = new DbModel();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface LoginUserListener{
        void onComplete(FirebaseUser user, Task<AuthResult> task);
    }

    public void loginUser(String email, String password, LoginUserListener listener ) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    listener.onComplete(user, task);
                });
    }

    public interface SignupUserListener{
        void onComplete(FirebaseUser user, Task task);
    }

    public void registerUser(User user, String password, SignupUserListener listener) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("users").document(userId);
                        Map<String, Object> dbUser = new HashMap<>();
                        dbUser.put("e_mail", user.getEmail());
                        dbUser.put("full_name", user.getFullName());

                        documentReference.set(dbUser).addOnCompleteListener(task1 -> {
                            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                            listener.onComplete(fbUser, task1);
                        });
                    }  else {
                        Log.d("ERR", "Error creating account");
                    }
                });
    }

    public interface UploadPetListener{
        void onComplete(String id, Task task);
    }

    public void uploadPet(Pets pets, Bitmap bitmap, UploadPetListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> dbPet = new HashMap<>();
        dbPet.put("type", pets.getType());
        dbPet.put("name_pet", pets.getPetName());
        dbPet.put("area", pets.getArea());
        dbPet.put("age", pets.getAge());
        dbPet.put("phone", pets.getPhone());

        dbPet.put("timestamp", FieldValue.serverTimestamp());

        DocumentReference petDocRef = db.collection("pets").document();

        petDocRef.set(dbPet).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DocumentReference userRef = db.collection("users").document(user.getUid());
                userRef.update("pets", FieldValue.arrayUnion(petDocRef)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        StorageReference storageRef = storage.getReference();
                        StorageReference imageRef = storageRef.child("images/" + petDocRef.getId() + ".jpg");

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = imageRef.putBytes(data);
                        uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Uri downloadUrl = uri;
                                    listener.onComplete( petDocRef.getId(), task);
                                }));
                    }
                });
            }
        });
    }

    public interface GetPetListener{
        void onComplete(Task task, Pets pet);
    }

    public void getPet(String petId ,GetPetListener listener ){
        db.collection("pets").document(petId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                   Pets p= Pets.petFromJson(document.getData());
                }
            }
        });
    }
}