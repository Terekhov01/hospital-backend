package com.NetCracker.Entities;

import javax.persistence.*;

@MappedSuperclass
public class User {

    @Column(name = "LAST_NAME")
    private String lastName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
