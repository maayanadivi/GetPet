package com.example.getpet.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.getpet.MyApplication;


@Entity
public class Pets implements Parcelable {
    @PrimaryKey
    @NonNull
    private String id = "";
    private String type, petName, area, age, phone, img;
    private boolean isDeleted;
    private Long lastUpdated = new Long(0);

    public Pets(){}

    public Pets(String type,String petName,String area,String age,String phone){
        this.type = type;
        this.petName = petName;
        this.area = area;
        this.age = age;
        this.phone = phone;
    }


    public String getType() {
        return type;
    }

    public String getPetName() {
        return petName;
    }

    public String getArea() {
        return area;
    }

    public String getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }

    public String getImg(){
        return img;
    }

    public String getId() {
        return id;
    }

    public void setImg(String image) {
        this.img = image;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) { this.id = id;}

    public void setPetName(String petName) { this.petName = petName; }

    public void setArea(String area) { this.area = area; }

    public void setAge(String age) { this.age = age; }

    public void setPhone(String phone) { this.phone = phone; }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    static Pets petFromJson(Map<String,Object> json){
        String name = (String) json.get("name_pet");
        String type = (String) json.get("type");
        String area = (String) json.get("area");
        String age = (String) json.get("age");
        String phone = (String) json.get("phone");
        String img = (String) json.get("img");

        Pets pet = new Pets(type,name,area,age,phone);
        pet.setImg(img);

        Timestamp ts = (Timestamp)json.get(Constants.LAST_UPDATED);
        pet.setLastUpdated(new Long(ts.getSeconds()));

        return pet;
    }

    static Long getLocalLastUpdated(){
        Long localLastUpdate = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong("POSTS_LAST_UPDATE",0);
        return localLastUpdate;
    }

    static void setLocalLastUpdated(Long date){
        SharedPreferences.Editor editor = MyApplication.getContext()
                .getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong("POSTS_LAST_UPDATE",date);
        editor.commit();
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public static final Creator<Pets> CREATOR = new Creator<Pets>() {
        @Override
        public Pets createFromParcel(Parcel in) {
            return new Pets(in);
        }

        @Override
        public Pets[] newArray(int size) {
            return new Pets[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(type);
        parcel.writeString(petName);
        parcel.writeString(area);
        parcel.writeString(age);
        parcel.writeString(phone);
        parcel.writeString(img);
        parcel.writeByte((byte) (isDeleted ? 1 : 0));
        if (lastUpdated == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(lastUpdated);
        }
    }

    protected Pets(Parcel in) {
        id = in.readString();
        type = in.readString();
        petName = in.readString();
        area = in.readString();
        age = in.readString();
        phone = in.readString();
        img = in.readString();

        isDeleted = in.readByte() != 0;
        if (in.readByte() == 0) {
            lastUpdated = null;
        } else {
            lastUpdated = in.readLong();
        }
    }

}
