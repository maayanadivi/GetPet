package com.example.getpet.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.getpet.Model.interfaces.DeletePetsListener;
import com.example.getpet.Model.interfaces.EditPetsListener;
import com.example.getpet.Model.interfaces.GetUserById;
import com.example.getpet.Model.interfaces.UploadImageListener;
import com.example.getpet.Model.interfaces.UploadPetListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
        Log.d("w", user.getEmail());
        Log.d("w", password);
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
                                    Log.d("IMG", url);
                                    dbPet.put("img", url);
                                    dbPet.put("ownerId", user.getUid());
                                    dbPet.put("isDeleted", false);

                                    petDocRef.set(dbPet).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pets.setImg(url);
                                            pets.setOwnerId(user.getUid());
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

    public void editPet(Pets pets, Bitmap bitmap, EditPetsListener listener) {
        DocumentReference docRef = db.collection("pets").document(pets.getId());
        if(bitmap == null) {
            docRef.set(pets.toJson()).addOnSuccessListener(unused -> listener.onComplete(pets));
        } else {
            uploadImage(bitmap, pets.getId(), url -> {
                pets.setImg(url);
                docRef.set(pets.toJson()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listener.onComplete(pets);
                    }
                });
            });
        }

    }

    public void deletePet(Pets pets, DeletePetsListener listener) {
        DocumentReference docRef = db.collection("pets").document(pets.getId());
        docRef.update("isDeleted", true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete();
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
                    Log.d("PET", "Not successful - didn't get all pets");
                }
                listener.onComplete(petsList);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ERROR!", "Not successful - didn't get all pets");
                listener.onComplete(null);
            }
        });
    }

    public void getUserById(String uid, GetUserById listener) {

        DocumentReference docRef = db.collection(Constants.FB_USER_COLLECTION).document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User u = User.fromJson(document.getData());
                    u.setId(uid);
                    listener.onComplete(u);
                } else {
                    listener.onComplete(null);
                }
            } else {
                listener.onComplete(null);
            }
        });
    }
    public void uploadImage(Bitmap bitmap, String id_key, final UploadImageListener listener)  {


        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imageRef;

        imageRef = storage.getReference().child(Constants.MODEL_FIRE_BASE_IMAGE_COLLECTION).child(id_key);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();
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