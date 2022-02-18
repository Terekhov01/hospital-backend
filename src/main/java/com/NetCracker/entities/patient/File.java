package com.NetCracker.entities.patient;

import com.NetCracker.entities.appointment.Appointment;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private byte[] fileData;

    private LocalDateTime creationDate;

    private String name;

    public File() {
    }

    public File(String name, Appointment appointment, byte[] fileData)
    {
        this.name = name;
        this.creationDate = LocalDateTime.now();
        this.appointment = appointment;
        this.fileData = new byte[fileData.length];
        System.arraycopy(fileData, 0, this.fileData, 0, fileData.length);
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

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = new byte[fileData.length];
        System.arraycopy(fileData, 0, this.fileData, 0, fileData.length);
    }

    public String getName()
    {
        return name;
    }

    public LocalDateTime getCreationDate()
    {
        return creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof File)) return false;
        File file = (File) o;
        return getId() == file.getId() && getAppointment().equals(file.getAppointment()) && Arrays.equals(getFileData(), file.getFileData());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getAppointment());
        result = 31 * result + Arrays.hashCode(getFileData());
        return result;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", appointment=" + appointment +
                ", file_data=" + Arrays.toString(fileData) +
                '}';
    }
}

