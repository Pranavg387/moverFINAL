package com.example.mover_f;

public class customer_rides_model {
    String pickup;
    String destinaion;

    public customer_rides_model(String pickup, String destinaion) {
        this.pickup = pickup;
        this.destinaion = destinaion;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getDestinaion() {
        return destinaion;
    }

    public void setDestinaion(String destinaion) {
        this.destinaion = destinaion;
    }
}
