package com.NetCracker.entities.doctor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Entity
@Data
@AllArgsConstructor
@Table(name = "doctors_rating")
public class DoctorRating {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
    private Long id;
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private UserStub user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Column
    private Integer rating;

    @Column
    private String feedback;

    @CreationTimestamp
    private Date created;
}