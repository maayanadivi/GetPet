package com.example.getpet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.getpet.Model.Model;
import com.example.getpet.Model.Pets;
import com.example.getpet.Model.User;

import java.util.List;

public class myProfile_FragmentViewModel extends ViewModel {
    LiveData<List<Pets>> data;
    String ownerId;

    public LiveData<List<Pets>> getData() {
        return data;
    }

    public void setData(String ownerId) {
        this.ownerId = ownerId;
        this.data = Model.instance.getUserPetsByOwnerId(ownerId);
    }
}
