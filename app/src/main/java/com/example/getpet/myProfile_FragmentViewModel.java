package com.example.getpet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.getpet.Model.Model;
import com.example.getpet.Model.Pets;
import com.example.getpet.Model.User;

import java.util.List;

public class myProfile_FragmentViewModel extends ViewModel {
    LiveData<List<Pets>> data;
    User user;

    public LiveData<List<Pets>> getData() {
        return data;
    }

    public void setData(User user) {
        this.user = user;

//        this.data = Model.instance.getUserPetsByEmail(user.getEmail());
    }
}
