package com.example.prifsweb.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Customer extends User {
    private String address;

    public Customer(String login, String password, String name, String surname, String phoneNumber,
                    LocalDateTime dateCreated, LocalDateTime dateUpdated, boolean isAdmin) {
        super(login, password, name, surname, phoneNumber, dateCreated, dateUpdated, isAdmin);
    }

    public Customer(String login, String password, String name, String surname, String phoneNumber,
                    LocalDateTime dateCreated, LocalDateTime dateUpdated, boolean isAdmin, String address) {
        super(login, password, name, surname, phoneNumber, dateCreated, dateUpdated, isAdmin);
        this.address = address;
    }
}
