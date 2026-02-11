package com.example.kursinisandroid.models;

import com.google.gson.annotations.SerializedName;

public class Driver extends User {
    @SerializedName("licence")
    private String licence;

    @SerializedName("bDate")
    private String bDate;

    @SerializedName("vehicleType")
    private VehicleType vehicleType;

    public Driver() {
    }

    public Driver(String login, String password, String name, String surname, String phoneNumber,
                  String licence, String bDate, VehicleType vehicleType) {
        super(login, password, name, surname, phoneNumber);
        this.licence = licence;
        this.bDate = bDate;
        this.vehicleType = vehicleType;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getbDate() {
        return bDate;
    }

    public void setbDate(String bDate) {
        this.bDate = bDate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
}
