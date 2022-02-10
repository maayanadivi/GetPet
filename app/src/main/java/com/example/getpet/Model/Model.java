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
        //1. get local last update
        Long localLastUpdate = Pets.getLocalLastUpdated();
        Log.d("TAG","localLastUpdate: " + localLastUpdate);
        //2. get all students record since local last update from firebase
        modelFirebase.getAllPets(localLastUpdate,(list)->{
            MyApplication.executorService.execute(()->{
                //3. update local last update date
                //4. add new records to the local db
                Long lLastUpdate = new Long(0);
                Log.d("TAG", "FB returned " + list.size());
                for(Pets s : list){
                    AppLocalDB.db.studentDao().insertAll(s);
                    if (s.getLastUpdated() > lLastUpdate){
                        lLastUpdate = s.getLastUpdated();
                    }
                }
                Pets.setLocalLastUpdated(lLastUpdate);

                //5. return all records to the caller
                List<Pets> stList = AppLocalDB.db.studentDao().getAll();
                petsList.postValue(stList);
                petsListLoadingState.postValue(LoadingState.loaded);
            });
        });
    }
}
