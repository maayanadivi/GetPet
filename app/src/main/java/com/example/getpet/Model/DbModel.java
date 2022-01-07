package com.example.getpet.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.getpet.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public interface UploadImageListener{
        void onComplete(FirebaseUser user, Task task);
    }

    public void uploadImage(Bitmap bitmap, String name, UploadImageListener listener ) {
        DatabaseReference storage = FirebaseDatabase.getInstance().getReference("Images");

        DatabaseReference imageRef =   storage.child("productImg" + name + ".jpg");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

//        byte[] byteArr = bytes.toByteArray();

//        UploadTask uploadTask = imageRef.putBytes(data);
//        uploadTask.addOnFailureListener(exception -> listener.onComplete(null))
//                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
//                        .addOnSuccessListener(uri -> {
//                            Uri downloadUrl = uri;
//                            listener.onComplete(downloadUrl.toString());
//                        }));


    }

}