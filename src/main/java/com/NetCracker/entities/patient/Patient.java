package com.NetCracker.entities.patient;

import com.NetCracker.entities.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Entity
@Getter
@Setter
@Table(name = "patients")
public class Patient {
    @Id
    private Long id;

    @OneToOne
    @MapsId
//    @JsonIgnore
    @JsonManagedReference
    private User user;

    // https://stackoverflow.com/a/40647805/12287688
    @Pattern(regexp = "^(?!^0+$)[a-zA-Z0-9]{3,20}$")
    @Column
    private String passport;

    @NotBlank
    @Pattern(regexp = "^\\d{16}$")
    @Column
    private String polys;

//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
//    @JoinColumn(name = "PATIENT")
//    @JsonManagedReference
//    private List<File> files;

    public Patient(String passport, String polys) {
        this.passport = passport;
        this.polys = polys;
    }

    public Patient(User user, String passport, String polys/*, List<File> files*/) {
        this.user = user;
        this.passport = passport;
        this.polys = polys;
//        this.files = files;
    }

    public Patient() {
    }
}
