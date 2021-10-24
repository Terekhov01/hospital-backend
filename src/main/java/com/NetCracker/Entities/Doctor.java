package Hospital.doctors.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Doctor {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Temporal(TemporalType.DATE)
    private Date dateOfEmployment;
//    @Temporal(TemporalType.DATE)
//    private Date dateOfBirth;
    //    private String name;
//    private String surname;
//    private String middlename;
    //@OneToMany(mappedBy = "doc")

    //private List<Response> responses;

    private String education;

    @OneToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false, updatable = false)
    private Room room;

    @OneToOne(optional = false)
    @JoinColumn(name = "specialization_id", nullable = false, updatable = false)
    private Specialist specialist;

//    @OneToOne(optional = false)
//    @JoinColumn(name = "department_id", nullable = false, updatable = false)
//    private Department department;


//unique = true
}