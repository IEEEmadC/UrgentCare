package com.example.peterbrayo.urgentcare;

public class NotificationModel {

    private String message;
    private double latitude;
    private double longitude;
    private String imageUrl;

    NotificationModel(){
    }


    public NotificationModel(String message, double latitude, double longitude, String imageUrl) {
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean exists() {
        if (((Double)(latitude)).toString().trim().length()!= 0 && ((Double)(longitude)).toString().trim().length()!= 0) {
            return true;
        }
        else{
            return false;
        }
    }
}
