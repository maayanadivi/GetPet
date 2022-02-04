package com.example.getpet.Model;

import android.net.Uri;

import com.google.firebase.Timestamp;

import java.util.Map;

public class Pets {
    String type, petName, area, age, phone;
    Uri image;
    public final static String LAST_UPDATED = "LAST_UPDATED";

    Long lastUpdated = new Long(0);

    final static String ID = "id";
    final static String AREA = "area";
    final static String NAME_PET = "name_pet";
    final static String PHONE = "phone";
    final static String TYPE = "type";
    final static String AGE = "age";
    final static String TIME = "timestamp";


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

    public Uri getImage(){return image;}

    public void setImage(Uri image) {
        this.image = image;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPetName(String petName) { this.petName = petName; }

    public void setArea(String area) { this.area = area; }

    public void setAge(String age) { this.age = age; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    static Pets petFromJson(Map<String,Object> json){

        String name = (String) json.get("name");
        String type = (String) json.get("type");
        String area = (String) json.get("area");
        String age = (String) json.get("age");
        String phone = (String) json.get("phone");

        Pets pet = new Pets(type,name,area,age,phone);
        Timestamp ts = (Timestamp)json.get(LAST_UPDATED);
        pet.setLastUpdated(new Long(ts.getSeconds()));

        return pet;
    }
}
