package com.example.peterbrayo.urgentcare;

public class ReplyModel {
    private String name;
    private String contact;
    private String image;
    private String msg;

    public ReplyModel(String name, String contact, String image, String msg) {
        this.name = name;
        this.contact = contact;
        this.image = image;
        this.msg = msg;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
