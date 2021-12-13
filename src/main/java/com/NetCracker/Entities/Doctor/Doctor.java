package com.NetCracker.Entities.Doctor;

import com.NetCracker.Entities.Schedule.DoctorSchedule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
//@RequiredArgsConstructor
//@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dateOfEmployment;

    private String education;

    @ToString.Exclude
    @ManyToOne()
    @JoinColumn(name = "room_id")
    private Room room;

    //    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "doctors_specialist",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "specialist_id"),
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    @ManyToMany(mappedBy = "doctors")
    @ToString.Exclude
    private Set<Specialist> specialist = new HashSet<>();

    @ToString.Exclude
    @OneToOne(mappedBy = "relatedDoctor")
    DoctorSchedule schedule;

    private String firstName;
    private String lastName;

    public Doctor(Date dateOfEmployment, String education, Room room, Set<Specialist> specialist, DoctorSchedule schedule, String firstName, String lastName, List<DoctorRating> ratings) {
        this.dateOfEmployment = dateOfEmployment;
        this.education = education;
        this.room = room;
        this.specialist = specialist;
        this.schedule = schedule;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ratings = ratings;
    }

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "doctor",cascade = CascadeType.ALL)
    private List<DoctorRating> ratings;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(id, doctor.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void addSpecialist(Specialist specialist) {
        this.getSpecialist().add(specialist);
        specialist.getDoctors().add(this);
    }

    public void addSpecialist(List<Specialist> specialist) {
        this.getSpecialist().addAll(specialist);
        specialist.forEach(x -> x.getDoctors().add(this));
    }
}
