package com.example.mover_f;

public class admin_customer_model {


    String name;
    String phone;
    String purl ;

    admin_customer_model(){


    }

    public admin_customer_model(  String name, String phone, String purl) {

        this.name = name;
        this.phone = phone;
        this.purl = purl;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }
}
