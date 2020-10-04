package com.example.mover_f;


public class adminHistoryObject {
    private String driverId;

    adminHistoryObject(){


    }
    public adminHistoryObject(String driverId){

                this.driverId = driverId;

    }

    public String getRideId(){return driverId;}

    public void setRideId(String rideId) {
        this.driverId = rideId;
    }

}
