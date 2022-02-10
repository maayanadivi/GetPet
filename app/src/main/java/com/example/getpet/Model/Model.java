package com.example.getpet.Model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.getpet.MyApplication;

import java.util.List;

public class Model {
    public static final Model instance = new Model();

    DbModel modelFirebase = new DbModel();
    private Model(){
        petsListLoadingState.setValue(LoadingState.loaded);
        reloadPetList();
    }

    public enum LoadingState{
        loading,
        loaded
    }

    public LiveData<LoadingState> getStudentListLoadingState(){
        return petsListLoadingState;
    }
    MutableLiveData<LoadingState> petsListLoadingState = new MutableLiveData<LoadingState>();
    MutableLiveData<List<Pets>> petsList = new MutableLiveData<List<Pets>>();

    public LiveData<List<Pets>> getAll(){
        return petsList;
    }

    public void reloadPetList() {
        petsListLoadingState.setValue(LoadingState.loading);
        Long localLastUpdate = Pets.getLocalLastUpdated();

        modelFirebase.getAllPets(localLastUpdate,(list)->{
            MyApplication.executorService.execute(()->{
                Long lLastUpdate = new Long(0);
                for(Pets pet : list){
                    if(!pet.isDeleted()) {
                        AppLocalDB.db.petsDao().insertAll(pet);
                    }
                    else {
                        AppLocalDB.db.petsDao().delete(pet);
                    }
                    if (pet.getLastUpdated() > lLastUpdate){
                        lLastUpdate = pet.getLastUpdated();
                    }
                }
                Pets.setLocalLastUpdated(lLastUpdate);

                List<Pets> stList = AppLocalDB.db.petsDao().getAll();
                petsList.postValue(stList);
                petsListLoadingState.postValue(LoadingState.loaded);
            });
        });
    }
}
