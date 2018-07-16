package com.example.peterbrayo.urgentcare;

public class ChatMessages {
    private  String text;
    private  String sender;
    private   String image;

    ChatMessages(){}


    ChatMessages(String text, String sender, String image) {
        this.text = text;
        this.sender = sender;
        this.image = image;

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
