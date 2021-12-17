//package Hospital.doctors.controller;
package com.NetCracker.controllers.doctor;



import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.services.doctor.DoctorUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping({"/doctorinfo"})
public class DoctorInfoController {

    @Autowired
    private DoctorUserService doctorUserService;

    @Autowired
    private DoctorRepository doctorRepository;
//
//    @GetMapping(path = {"/{id}"})
//    public List<Doctor> findAll(Integer id){
//
//        System.out.println("дошло");
//        return doctorUserService.findAll();
//    }

//    @GetMapping("/{id}")
//    public String DoctorInfo(@PathVariable Integer id) throws ResourceNotFoundException {
//        Doctor doctorUser = doctorUserRepository.findById(id).get();
//        //return ResponseEntity.ok(doctorUser);
//        return"redirect:/product/"+doctorUserRepository.findById(id);
//    }

    @GetMapping(path = {"/{id}"})
    public Doctor findOne(@PathVariable("id") Long id){
        return doctorUserService.findById(id);
    }

}
