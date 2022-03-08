//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.NetCracker.controllers;

import com.NetCracker.controllers.exception.ResourceNotFoundException;
import com.NetCracker.entities.MedCard;
import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.appointment.AppointmentRegistration;
import com.NetCracker.entities.user.role.ERole;
import com.NetCracker.entities.user.role.Role;
import com.NetCracker.entities.user.User;
import com.NetCracker.repositories.MedCardRepo;
import com.NetCracker.repositories.appointment.AppointmentRegistrationRepo;
import com.NetCracker.repositories.appointment.AppointmentRepo;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.services.user.UserDetailsImpl;
import com.NetCracker.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping({"api/medCard"})
@CrossOrigin(
        origins = {"http://localhost:4200"}
)
public class MedCardController {
    @Autowired
    private MedCardRepo medCardRepo;
    @Autowired
    private AppointmentRepo appointmentRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private AppointmentRegistrationRepo appointmentRegistrationRepo;

    public MedCardController() {

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping({"{id}"})
    public ResponseEntity<MedCard> getInfo(@PathVariable("id") Long id) {
        MedCard medCard = this.medCardRepo.findByPatient_Id(id);
        if(getAccess(id)){
            List<Appointment> appointments = appointmentRepo.findAll();
            appointments.stream()
                    .filter(appointment -> Objects.equals(appointment.getAppointmentRegistration().getPatient().getId(), medCard.getPatient().getId()))
                    .forEach(medCard::addAppointments);
            medCardRepo.save(medCard);
            return new ResponseEntity(medCard, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Access denied");
        //return new ResponseEntity(HttpStatus.LOCKED);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("hereditary/{id}")
    public ResponseEntity<String> getHereditary(@PathVariable("id") Long id) {
        MedCard medCard = this.medCardRepo.findByPatient_Id(id);
        if(getAccess(id)){
            return new ResponseEntity(medCard.getHereditary(), HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Access denied");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("contraindications/{id}")
    public ResponseEntity<String> getContr(@PathVariable("id") Long id) {
        MedCard medCard = this.medCardRepo.findByPatient_Id(id);
        if(getAccess(id)){
            return new ResponseEntity(medCard.getContraindications(), HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Access denied");
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping({"edit-hereditary/{id}"})
    public ResponseEntity<MedCard> editHereditary(@RequestBody String hereditary, @PathVariable("id") Long id) {
        MedCard medCard = this.medCardRepo.findByPatient_Id(id);
        if(getAccess(id)){
            medCard.setHereditary(hereditary);
            this.medCardRepo.save(medCard);
            return new ResponseEntity(medCard, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Access denied");
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping({"edit-contr/{id}"})
    public ResponseEntity<MedCard> editContr(@RequestBody String contraindications, @PathVariable("id") Long id) {
        MedCard medCard = this.medCardRepo.findByPatient_Id(id);
        if (getAccess(id)){
            medCard.setContraindications(contraindications);
            this.medCardRepo.save(medCard);
            return new ResponseEntity(medCard, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("Access denied");
    }

    public boolean getAccess(Long id){
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentId = userDetailsImpl.getId();
        User currentUser = userService.findById(currentId);
        boolean rightDoctor = false;
        for (Role role : currentUser.getRoles()){
            if(role.getName() == ERole.ROLE_DOCTOR){
                List<AppointmentRegistration> registrationList = appointmentRegistrationRepo.findAllByDoctor(currentId);
                for(AppointmentRegistration registration : registrationList){
                    rightDoctor = Objects.equals(registration.getPatient().getId(), id);
                    if (rightDoctor) return true;
                }
            }
            else if(role.getName() == ERole.ROLE_ADMIN ||
                    role.getName() == ERole.ROLE_USER && Objects.equals(id, currentId)){
                return true;
            }
        }
        return false;
    }
}
