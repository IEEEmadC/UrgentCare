package com.example.peterbrayo.urgentcare;

class VolunteerLocation {
    private double latitude;
    private double longitude;

    public VolunteerLocation(double latitude, double longitude) {
    this.longitude = latitude;
    this.longitude = longitude;
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

    public boolean exists(){
        if(getLatitude() != 0 && getLongitude() != 0){
            return  true;
        }
        else{
            return false;
        }
    }
}
