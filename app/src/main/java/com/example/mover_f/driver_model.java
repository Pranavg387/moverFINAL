package com.example.mover_f;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class driver_model {
    String adminId;
    String car;
    String name;
    String phone;
    String service;
    String purl;



    driver_model(){


    }


    public driver_model( String adminId, String car, String name, String phone, String service, String purl) {

        this.adminId = adminId;
        this.car = car;
        this.name = name;
        this.phone = phone;
        this.service = service;
        this.purl = purl;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getService() {
        return service;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getCar() {
        return car;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setCar(String car) {
        this.car = car;
    }
}
