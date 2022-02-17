package com.example.getpet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.getpet.Model.Model;
import com.example.getpet.Model.Pets;

import java.util.List;

public class homepage_FragmentViewModel extends ViewModel {

    LiveData<List<Pets>> data = Model.instance.getAll();


    public LiveData<List<Pets>> getData() {
        return data;
    }
}
