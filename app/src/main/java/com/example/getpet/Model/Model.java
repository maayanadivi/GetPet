package com.example.getpet.Model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.getpet.Model.interfaces.DeletePetsListener;
import com.example.getpet.Model.interfaces.EditPetsListener;
import com.example.getpet.Model.interfaces.GetUserById;
import com.example.getpet.MyApplication;
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

        modelFirebase.getAllPets(localLastUpdate, (list) -> {
            if (list != null) {
                MyApplication.executorService.execute(() -> {
                    Long lastUpdate = new Long(0);
                    for (Pets pet : list) {
                        Log.d("POST DELETED ", pet.isDeleted() + "" );
                        if (!pet.isDeleted()) {
                            AppLocalDB.db.petsDao().insertAll(pet);
                        } else {
                            AppLocalDB.db.petsDao().delete(pet);
                        }
                        if (pet.getLastUpdated() > lastUpdate) {
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

    public void getUserById(String userId, GetUserById listener) {
        DbModel.dbIns.getUserById(userId, listener);
    }

    public LiveData<List<Pets>> getUserPetsByOwnerId(String ownerId) {
        return AppLocalDB.db.petsDao().getPetsByOwnerId(ownerId);
    }

    public void editPost(Pets pets, Bitmap bitmap, EditPetsListener listener) {
        modelFirebase.editProduct(pets, bitmap, listener);
    }

    public void deletePet(Pets pets, DeletePetsListener listener) {
        pets.setDeleted(true);
        modelFirebase.deletePet(pets, new DeletePetsListener() {
            @Override
            public void onComplete() {
                pets.setDeleted(true);
                reloadPetList();
                listener.onComplete();
            }
        });
    }

}
