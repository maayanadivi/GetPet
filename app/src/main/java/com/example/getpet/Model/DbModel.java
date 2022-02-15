package com.example.getpet.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.getpet.Model.interfaces.UploadImageListener;
import com.example.getpet.Model.interfaces.UploadPetListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DbModel {
    public static final DbModel dbIns = new DbModel();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface LoginUserListener {
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

    public void uploadPet(Pets pets, Bitmap bitmap, UploadPetListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> dbPet = new HashMap<>();
        DocumentReference petDocRef = db.collection("pets").document();

        petDocRef.set(dbPet).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DocumentReference userRef = db.collection("users").document(user.getUid());
                userRef.update("pets", FieldValue.arrayUnion(petDocRef)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        uploadImage(bitmap, petDocRef.getId(), new UploadImageListener() {
                            @Override
                            public void onComplete(String url) {
                                if (url != null) {
                                    dbPet.put("type", pets.getType());
                                    dbPet.put("name_pet", pets.getPetName());
                                    dbPet.put("area", pets.getArea());
                                    dbPet.put("age", pets.getAge());
                                    dbPet.put("phone", pets.getPhone());
                                    dbPet.put("timestamp", FieldValue.serverTimestamp());
                                    dbPet.put("img", url);
                                    petDocRef.set(dbPet).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pets.setImg(url);
                                            listener.onComplete(task, pets);
                                        }
                                    });
                                } else {
                                    listener.onComplete(task, new Pets());
                                    // pet will not be initialized and we will know that there was error.
                                }
                            }
                        });
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
                   Pets p = Pets.petFromJson(document.getData());
                   p.setId(document.getId());
                }
            }
        });
    }

    public interface GetAllPetsListener{
        void onComplete(List<Pets> data);
    }

    public void getAllPets(Long since, GetAllPetsListener listener) {
        db.collection("pets")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                LinkedList<Pets> petsList = new LinkedList<Pets>();
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        Pets p = Pets.petFromJson(doc.getData());
                        p.setId(doc.getId());

                        if (p != null) {
                            petsList.add(p);
                        }
                    }
                }else {
                    Log.d("PET", "Not successfull - didn't get all pets");
                }
                listener.onComplete(petsList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ERROR!", "Not successfullll - didn't get all pets");
                listener.onComplete(null);
            }
        });
    }

    public void uploadImage(Bitmap bitmap, String id_key, final UploadImageListener listener)  {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imageRef;
        byte[] data = baos.toByteArray();

        imageRef = storage.getReference().child(Constants.MODEL_FIRE_BASE_IMAGE_COLLECTION).child(id_key);
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);

        UploadTask uploadTask=imageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listener.onComplete(uri.toString());
                    }
                });
            }
        });
    }

}