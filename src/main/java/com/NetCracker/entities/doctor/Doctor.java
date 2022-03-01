package com.NetCracker.entities.doctor;

import com.NetCracker.entities.schedule.DoctorSchedule;
import com.NetCracker.entities.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
//@RequiredArgsConstructor
//@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doctor")
public class Doctor
{
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    @JsonIgnore
    private User user;

    @Temporal(TemporalType.DATE)
    private Date dateOfEmployment;

    private String education;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    //    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "doctors_specialist",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "specialist_id"),
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @JsonIgnore
    @ToString.Exclude
    private Set<Specialist> specialist = new HashSet<>();

    @ToString.Exclude
    @OneToOne(mappedBy = "relatedDoctor", cascade = CascadeType.REMOVE)
    @JsonBackReference
    DoctorSchedule schedule;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "doctor",cascade = CascadeType.ALL)
    private List<DoctorRating> ratings;

    public Doctor(Date dateOfEmployment, String education, Room room, Set<Specialist> specialist, DoctorSchedule schedule, String firstName, String lastName, List<DoctorRating> ratings) {
        this.dateOfEmployment = dateOfEmployment;
        this.education = education;
        this.room = room;
        this.specialist = specialist;
        this.schedule = schedule;
        user.setFirstName(firstName);
        user.setLastName(lastName);
        this.ratings = ratings;
    }

    public Doctor(Date dateOfEmployment, String education, Room room, Set<Specialist> specialist, DoctorSchedule schedule, String firstName, String lastName, List<DoctorRating> ratings, User user, Long id) {
        this.dateOfEmployment = dateOfEmployment;
        this.education = education;
        this.room = room;
        this.specialist = specialist;
        this.schedule = schedule;
        this.user = user;
        this.user.setFirstName(firstName);
        this.user.setLastName(lastName);
        this.ratings = ratings;
        this.id = id;
    }

    public Doctor(String education, Room room, Set<Specialist> specialist, User user)
    {
        this.user = user;
        this.dateOfEmployment = new Date();
        this.education = education;
        this.room = room;
        this.specialist = specialist;
        this.schedule = null;
        this.ratings = new ArrayList<>();
    }

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
