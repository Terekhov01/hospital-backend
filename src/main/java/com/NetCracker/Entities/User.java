package com.NetCracker.Entities;

import javax.persistence.*;

@MappedSuperclass
public class User {

    @Column(name = "LAST_NAME")
    private String lastName;

    public User() {
    }

    public User(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
