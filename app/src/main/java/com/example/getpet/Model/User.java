package com.example.getpet.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Map;

@Entity
public class User  implements Parcelable {
    @PrimaryKey
    @NonNull
    String email;
    String fullName;
    String id;

    public User(){}

    public User(String email, String fullName){
        this.email = email;
        this.fullName = fullName;

    }

    protected User(Parcel in) {
        email = in.readString();
        fullName = in.readString();
        id = in.readString();
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id){
        this.id = id ;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    static User fromJson(Map<String,Object> json) {
        String name = (String)json.get("full_name");
        String email = (String)json.get("e_mail");
        User u = new User(name, email);
        return u;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(email);
        parcel.writeString(fullName);
        parcel.writeString(id);
    }
}