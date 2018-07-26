package com.example.peterbrayo.urgentcare;

public class ChatMessages {
    private  String text;
    private  String sender;
    private   String image;
    private String profilePic;

    ChatMessages(){}


    ChatMessages(String text, String sender, String image, String profilePic) {
        this.text = text;
        this.sender = sender;
        this.image = image;
        this.profilePic = profilePic;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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
