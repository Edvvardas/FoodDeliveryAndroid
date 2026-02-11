package com.example.prifsweb.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Driver extends User {
    private String licence;
    private LocalDate bDate;
    private VehicleType vehicleType;


    public Driver(String login, String password, String name, String surname, String phoneNumber,
                  LocalDateTime dateCreated, LocalDateTime dateUpdated, boolean isAdmin,
                  String licence, LocalDate bDate, VehicleType vehicleType) {
        super(login, password, name, surname, phoneNumber, dateCreated, dateUpdated, isAdmin);
        this.licence = licence;
        this.bDate = bDate;
        this.vehicleType = vehicleType;
    }

}
