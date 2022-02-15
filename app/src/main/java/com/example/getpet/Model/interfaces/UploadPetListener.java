package com.example.getpet.Model.interfaces;

import com.example.getpet.Model.Pets;
import com.google.android.gms.tasks.Task;

public interface UploadPetListener {
    void onComplete(Task task, Pets pet);
}
