package com.NetCracker.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name  = "patients")
@Getter
@Setter

public class Patient {
    @Id
    private Long id;
    @OneToOne
    @MapsId
    @JsonIgnore
    private User user;
    @Column
    private String passport;
    @Column
    private String polys;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "PATIENT")
    @JsonManagedReference
    private List<File> files;

    public Patient(String passport, String polys) {
        this.passport = passport;
        this.polys = polys;
    }

    public Patient(User user, String passport, String polys, List<File> files) {
        this.user = user;
        this.passport = passport;
        this.polys = polys;
        this.files = files;
    }

    public Patient() {
    }
}
