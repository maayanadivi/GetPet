package com.example.getpet.Model;

public class User {
    String email,fullName;

    public User(){}

    public User(String email, String fullName){
        this.email = email;
        this.fullName = fullName;

    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}