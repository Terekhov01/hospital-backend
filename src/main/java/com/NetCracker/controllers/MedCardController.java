//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.NetCracker.controllers;

import com.NetCracker.entities.MedCard;
import com.NetCracker.entities.appointment.Appointment;
import com.NetCracker.repositories.MedCardRepo;
import com.NetCracker.repositories.appointment.AppointmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"medCard"})
@CrossOrigin(
        origins = {"http://localhost:4200"}
)
public class MedCardController {
    @Autowired
    private MedCardRepo medCardRepo;
    @Autowired
    private AppointmentRepo appointmentRepo;

    public MedCardController() {
    }

    @GetMapping({"{id}"})
    public ResponseEntity<MedCard> getInfo(@PathVariable("id") int id) {
        MedCard medCard = (MedCard)this.medCardRepo.findById(id).get();
        List<Appointment> appointments = appointmentRepo.findAll();
        appointments.stream()
                .filter(appointment -> appointment.getAppointmentRegistration().getPatient().getId() == medCard.getPatient().getId())
                .forEach(medCard::addAppointments);
        medCardRepo.save(medCard);
        return new ResponseEntity(medCard, HttpStatus.OK);
    }


    @GetMapping("{id}/hereditary")
    public ResponseEntity<String> getHereditary(@PathVariable("id") int id) {
        MedCard medCard = (MedCard)this.medCardRepo.findById(id).get();
        return new ResponseEntity(medCard.getHereditary(), HttpStatus.OK);
    }

    @GetMapping("{id}/contraindications")
    public ResponseEntity<String> getContr(@PathVariable("id") int id) {
        MedCard medCard = (MedCard)this.medCardRepo.findById(id).get();
        return new ResponseEntity(medCard.getContraindications(), HttpStatus.OK);
    }

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
