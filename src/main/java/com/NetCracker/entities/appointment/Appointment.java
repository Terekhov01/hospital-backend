package com.NetCracker.entities.appointment;

import com.NetCracker.entities.patient.File;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Type;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "APPOINTMENT_ID", unique = true, nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "APPOINTMENT_REGISTRATION",
            referencedColumnName = "APPOINTMENT_REGISTRATION_ID")
    private AppointmentRegistration appointmentRegistration;

//    @OneToOne
//    @JoinColumn(name = "FILE", referencedColumnName = "FILE_ID")
//    private File file;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "PATIENT", referencedColumnName = "PATIENT_ID")
//    private Patient patient;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "DOCTOR", referencedColumnName = "DOCTOR_ID")
//    private Doctor doctor;

//    @Column(name = "FILE", columnDefinition="BLOB")
//    @Lob
//    @Type(type = "org.hibernate.type.BlobType")
//    private byte[] file;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "DESCRIPTION")
    private String description;

//    @Column(name = "SERVICE")
//    private String service;

    @Column(name = "RECIPE")
    private String recipe;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "TREATMENT_PLAN")
    private String treatPlan;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "REHABILITATION_PLAN")
    private String rehabPlan;

    @Column(name = "DOCTORS_STATEMENT")
    private String docStatement;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "APPOINTMENT")
    @JsonManagedReference
    private List<File> files;

    public Appointment() {
    }

    public Appointment(Long id, AppointmentRegistration appointmentRegistration,
//                       Patient patient, Doctor doctor,
                       String description,
//                       File file,
//                       String service,
                       String recipe, String treatPlan,
                       String rehabPlan, String docStatement, List<File> files) {//,
//                       byte[] file) {
        this.id = id;
        this.appointmentRegistration = appointmentRegistration;
//        this.file = file;
//        this.patient = patient;
//        this.doctor = doctor;
        this.description = description;
//        this.service = service;
        this.recipe = recipe;
        this.treatPlan = treatPlan;
        this.rehabPlan = rehabPlan;
        this.docStatement = docStatement;
        this.files = files;
//        this.file = new byte[file.length];
//        System.arraycopy(file, 0, this.file, 0, file.length);
    }

    public Appointment(Appointment appointment) {
        this.files = appointment.getFiles();
        this.appointmentRegistration = appointment.getAppointmentRegistration();
        this.description = appointment.getDescription();
        this.docStatement = appointment.getDocStatement();
        this.id = appointment.getId();
        this.recipe = appointment.getRecipe();
        this.rehabPlan = appointment.getRehabPlan();
        this.treatPlan = appointment.getTreatPlan();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    //    public File getFile() {
//        return file;
//    }
//
//    public void setFile(File file) {
//        this.file = file;
//    }

    //    public Patient getPatient() {
//        return patient;
//    }
//
//    public void setPatient(Patient patient) {
//        this.patient = patient;
//    }
//
//    public Doctor getDoctor() {
//        return doctor;
//    }
//
//    public void setDoctor(Doctor doctor) {
//        this.doctor = doctor;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public String getService() {
//        return service;
//    }

//    public void setService(String service) {
//        this.service = service;
//    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public String getTreatPlan() {
        return treatPlan;
    }

    public void setTreatPlan(String treatPlan) {
        this.treatPlan = treatPlan;
    }

    public String getRehabPlan() {
        return rehabPlan;
    }

    public void setRehabPlan(String rehabPlan) {
        this.rehabPlan = rehabPlan;
    }

    public String getDocStatement() {
        return docStatement;
    }

    public void setDocStatement(String docStatement) {
        this.docStatement = docStatement;
    }

    public AppointmentRegistration getAppointmentRegistration() {
        return appointmentRegistration;
    }

    public void setAppointmentRegistration(AppointmentRegistration appointmentRegistration) {
        this.appointmentRegistration = appointmentRegistration;
    }

//    public byte[] getFile() {
//        if (this.file == null) {
//            this.file = new byte[1];
//        }
//        return file;
//    }
//
//    public void setFile(byte[] file) {
//        this.file = new byte[file.length];
//        System.arraycopy(file, 0, this.file, 0, file.length);
//    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", appointmentRegistration=" + appointmentRegistration +
//                ", patient=" + patient +
//                ", doctor=" + doctor +
                ", description='" + description + '\'' +
//                ", service='" + service + '\'' +
                ", recipe='" + recipe + '\'' +
                ", treatPlan='" + treatPlan + '\'' +
                ", rehabPlan='" + rehabPlan + '\'' +
                ", docStatement='" + docStatement + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment)) return false;
        Appointment that = (Appointment) o;
        return getId() == that.getId() && appointmentRegistration.equals(that.appointmentRegistration) &&
//                getPatient().equals(that.getPatient()) && getDoctor().equals(that.getDoctor()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
//                Objects.equals(getService(), that.getService()) &&
                Objects.equals(getRecipe(), that.getRecipe()) &&
//                Objects.equals(getFile(), that.getFile()) &&
//                Objects.equals(getFile(), that.getFile()) &&
                Objects.equals(getTreatPlan(), that.getTreatPlan()) && Objects.equals(getRehabPlan(),
                that.getRehabPlan()) && Objects.equals(getDocStatement(), that.getDocStatement());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), appointmentRegistration,
//                getPatient(), getDoctor(),
                getDescription(),
//                getService(),
                getRecipe(), getTreatPlan(), getRehabPlan(), getDocStatement());
    }

}
