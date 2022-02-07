package com.NetCracker.entities.patient;

import com.NetCracker.entities.appointment.Appointment;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "file")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "FILE_ID", unique = true, nullable = false)
    private Long id;



//    @OneToOne
//    @JoinColumn(name = "APPOINTMENT",
//            referencedColumnName = "APPOINTMENT_ID")
//    private Appointment appointment;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "PATIENT", referencedColumnName = "user_id")
//    @JsonBackReference
//    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPOINTMENT", referencedColumnName = "appointment_id")
    @JsonBackReference
    private Appointment appointment;

    @Column(name = "FILE_DATA")//, columnDefinition="BLOB")
//    @Lob
//    @Type(type = "org.hibernate.type.BlobType")
//    @Type(type="org.hibernate.type.PrimitiveByteArrayBlobType")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] file_data;

    public File() {
    }

    public File(Appointment appointment, byte[] file_data) {
        this.appointment = appointment;
        this.file_data = new byte[file_data.length];
        System.arraycopy(file_data, 0, this.file_data, 0, file_data.length);
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getFile_data() {
        return file_data;
    }

    public void setFile_data(byte[] file_data) {
        this.file_data = new byte[file_data.length];
        System.arraycopy(file_data, 0, this.file_data, 0, file_data.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof File)) return false;
        File file = (File) o;
        return getId() == file.getId() && getAppointment().equals(file.getAppointment()) && Arrays.equals(getFile_data(), file.getFile_data());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getAppointment());
        result = 31 * result + Arrays.hashCode(getFile_data());
        return result;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", appointment=" + appointment +
                ", file_data=" + Arrays.toString(file_data) +
                '}';
    }
}

