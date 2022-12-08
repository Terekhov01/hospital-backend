//package Hospital.doctors.controller;
package com.NetCracker.controllers.doctor;


import com.NetCracker.domain.DTO.DoctorUnionUserDto;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.user.User;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.services.doctor.DoctorUserService;
import com.NetCracker.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
//@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
@RequestMapping({"/doctorinfo"})
public class DoctorInfoController {

    @Autowired
    private DoctorUserService doctorUserService;

    @Autowired
    private DoctorRepository doctorRepository;

    private final UserService userService;
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
    @PreAuthorize("permitAll()")
    // Доступ в DoctorInfo
    @GetMapping(path = {"/{id}"})
    //DoctorUnionUserDto
    public DoctorUnionUserDto findOne(@PathVariable("id") Long id) {

//        if(id != null)
//        {
//            Doctor doctorUser = doctorRepository.findById(id).get();

//            doctorUser.getUser().getFirstName();
//            doctorUser.getUser().getLastName();
//            User simpleUser = userService.findById(id);
//            String name = simpleUser.getFirstName();
//            String lastName = simpleUser.getLastName();
//            return doctorRepository.findById(id).get();

            Optional<Doctor> byId = doctorRepository.findById(id);
//            return byId.map(x->new DoctorUnionUserDto(x,byId1)).orElse(null);


//        }

        System.out.println();

//        return doctorUserService.findById(id);
        return byId.map(DoctorUnionUserDto::new).orElse(null);
    }

}
