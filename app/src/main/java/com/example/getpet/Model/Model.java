package com.example.getpet.Model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.getpet.Model.interfaces.UploadImageListener;
import com.example.getpet.Model.interfaces.UploadPetListener;
import com.example.getpet.MyApplication;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class Model {
    public static final Model instance = new Model();
    DbModel modelFirebase = new DbModel();
    MutableLiveData<LoadingState> petsListLoadingState = new MutableLiveData<LoadingState>();
    MutableLiveData<List<Pets>> petsList = new MutableLiveData<>();

    private Model(){
        petsListLoadingState.setValue(LoadingState.loaded);
        reloadPetList();
    }

    public enum LoadingState{
        loading,
        loaded
    }

    public LiveData<LoadingState> getPetsLoadingState(){
        return petsListLoadingState;
    }

    public LiveData<List<Pets>> getAll(){
        return petsList;
    }

    public void reloadPetList() {
        petsListLoadingState.setValue(LoadingState.loading);
        Long localLastUpdate = Pets.getLocalLastUpdated();

        modelFirebase.getAllPets(localLastUpdate,(list)-> {
            if(list != null) {
                MyApplication.executorService.execute(()-> {
                    Long lastUpdate = new Long(0);
                    for(Pets pet : list) {
                        if(!pet.isDeleted()) {
                            AppLocalDB.db.petsDao().insertAll(pet);
                        }
                        else {
                            AppLocalDB.db.petsDao().delete(pet);
                        }
                        if (pet.getLastUpdated() > lastUpdate){
                            lastUpdate = pet.getLastUpdated();
                        }
                    }

                    Pets.setLocalLastUpdated(lastUpdate);
                    List<Pets> petList = AppLocalDB.db.petsDao().getAll();

                    petsList.postValue(petList);
                    petsListLoadingState.postValue(LoadingState.loaded);
                });
            }
        });
    }

    public void uploadImage(Bitmap bitmap, String name, final UploadImageListener listener){
        modelFirebase.uploadImage(bitmap,name,listener);
    }

//    public LiveData<List<Pets>> getUserPetsByEmail(String email) {
//        return AppLocalDB.db.userDao().getUserPetsByEmail(email);
//    }

    public void addPet(Pets pet, Bitmap bitmap, UploadPetListener listener) {
        modelFirebase.uploadPet(pet, bitmap, new UploadPetListener() {
            @Override
            public void onComplete(Task task, Pets pet) {
                reloadPetList();
                listener.onComplete(task, pet);
            }
        });
    }

}
