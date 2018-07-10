package com.example.peterbrayo.urgentcare;

public class VolunteerLocation {
    private Double latitude;
    private Double longitude;

    VolunteerLocation(){

    }

    VolunteerLocation(double lat, double lon){
        latitude = lat;
        longitude = lon;
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

    public boolean exists() {
        if (this.latitude.toString().trim().length()!=0 && this.longitude.toString().trim().length()!=0) {
            return true;
        }
        else{
            return false;
        }
    }
}
