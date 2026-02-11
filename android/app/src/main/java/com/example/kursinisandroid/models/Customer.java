package com.example.kursinisandroid.models;

import com.google.gson.annotations.SerializedName;

public class Customer extends User {
    @SerializedName("address")
    protected String address;

    public Customer() {
    }

    public Customer(String login, String password, String name, String surname, String phoneNumber, String address) {
        super(login, password, name, surname, phoneNumber);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
