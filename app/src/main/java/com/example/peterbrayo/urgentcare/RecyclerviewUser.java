package com.example.peterbrayo.urgentcare;

import java.util.ArrayList;

public class RecyclerviewUser {
    private String name;
    private String contact;
    private int rating;
    private String image;

    public RecyclerviewUser(String name, String contact, int rating, String image) {
        this.name = name;
        this.contact = contact;
        this.rating = rating;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean imageExists(){
        if(!getImage().equals("")){
            return true;
        }
        else{
            return false;
        }
//        !getImage().equals("");
    }
}
