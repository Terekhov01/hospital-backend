//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.NetCracker.controllers;

import com.NetCracker.controllers.exception.ResourceNotFoundException;
import com.NetCracker.entities.MedCard;
import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.entities.user.ERole;
import com.NetCracker.entities.user.Role;
import com.NetCracker.entities.user.User;
import com.NetCracker.repositories.MedCardRepo;
import com.NetCracker.repositories.appointment.AppointmentRepo;
import com.NetCracker.services.user.UserDetailsImpl;
import com.NetCracker.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
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

    private UserDetailsImpl userDetailsImpl;
    private Long currentId;
    private User currentUser;

    public MedCardController() {

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping({"{id}"})
    public ResponseEntity<MedCard> getInfo(@PathVariable("id") Long id, Authentication authentication) {
        userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        currentId = userDetailsImpl.getId();
        currentUser = userService.findById(currentId);
        MedCard medCard = this.medCardRepo.findByPatient_Id(id);
        if (userDetailsImpl != null){
            for (Role role : currentUser.getRoles()){
                if(role.getName() == ERole.ROLE_ADMIN ||
                    role.getName() == ERole.ROLE_DOCTOR ||
                    role.getName() == ERole.ROLE_USER && Objects.equals(id, currentId)){
                    List<Appointment> appointments = appointmentRepo.findAll();
                    appointments.stream()
                            .filter(appointment -> Objects.equals(appointment.getAppointmentRegistration().getPatient().getId(), medCard.getPatient().getId()))
                            .forEach(medCard::addAppointments);
                    medCardRepo.save(medCard);
                    return new ResponseEntity(medCard, HttpStatus.OK);
                }
            }
        }
        throw new ResourceNotFoundException("Access denied");
        //return new ResponseEntity(HttpStatus.LOCKED);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("{id}/hereditary")
    public ResponseEntity<String> getHereditary(@PathVariable("id") int id) {
        MedCard medCard = (MedCard)this.medCardRepo.findById(id).get();
        return new ResponseEntity(medCard.getHereditary(), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("{id}/contraindications")
    public ResponseEntity<String> getContr(@PathVariable("id") int id) {
        MedCard medCard = (MedCard)this.medCardRepo.findById(id).get();
        return new ResponseEntity(medCard.getContraindications(), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping({"{id}/edit-hereditary"})
    public ResponseEntity<MedCard> editHereditary(@RequestBody String hereditary, @PathVariable("id") int id) {
        MedCard medCard = (MedCard)this.medCardRepo.findById(id).get();
        medCard.setHereditary(hereditary);
        this.medCardRepo.save(medCard);
        return new ResponseEntity(medCard, HttpStatus.OK);
    }

    @PutMapping({"{id}/edit-contr"})
    public ResponseEntity<MedCard> editContr(@RequestBody String contraindications, @PathVariable("id") int id) {
        MedCard medCard = (MedCard)this.medCardRepo.findById(id).get();
        medCard.setContraindications(contraindications);
        this.medCardRepo.save(medCard);
        return new ResponseEntity(medCard, HttpStatus.OK);
    }
}
