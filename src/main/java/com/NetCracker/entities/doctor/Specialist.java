package com.NetCracker.entities.doctor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString

public class Specialist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String specialization;

    @JsonIgnore
    @ManyToMany(mappedBy = "specialist", cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @ToString.Exclude
    private Set<Doctor> doctors;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Specialist that = (Specialist) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}