package com.NetCracker.controllers.doctor;


import com.NetCracker.domain.DTO.DoctorUnionUserDto;
import com.NetCracker.domain.DTO.UserDto;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.controllers.exception.ResourceNotFoundException;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.services.doctor.DoctorUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//import javax.validation.Valid;

import java.util.List;
@AllArgsConstructor
//@CrossOrigin(origins = "*", maxAge = 3600)
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
//@CrossOrigin(origins = "http://localhost:4200")
//@RequestMapping({"/api"})
@RequestMapping({"/doctorusers"})
public class DoctorUserController {

    @Autowired
    private DoctorUserService doctorUserService;

    @Autowired
    private DoctorRepository doctorUserRepository;

//    @PreAuthorize("hasRole('ROLE_DOCTOR')")
//@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("permitAll()")
    @PostMapping
    public Doctor create(@RequestBody UserDto user){
        System.out.println(user);
        return doctorUserService.create(user);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("permitAll()")
    @GetMapping(path = {"/{id}"})
    public DoctorUnionUserDto findOne(@PathVariable("id") Long id){
        return doctorUserService.findDoctorWithName(id);

    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("permitAll()")
    @PutMapping(path = {"/{id}"})
    public DoctorUnionUserDto update( @RequestBody DoctorUnionUserDto user){
        return doctorUserService.update(user);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("permitAll()")
    @DeleteMapping(path ={"/{id}"})
    public void delete(@PathVariable("id") Long id) {
        doctorUserService.delete(id);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("permitAll()")
    @GetMapping
    public List<DoctorUnionUserDto> findAll(){
        return doctorUserService.findAllDoctorWithName();
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("permitAll()")
    @GetMapping("/update-doctor/{id}")
    public ResponseEntity<Doctor> getEmployeeById(@PathVariable Long id) throws ResourceNotFoundException {
        Doctor doctorUser = doctorUserRepository.findById(id).get();
        return ResponseEntity.ok(doctorUser);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("permitAll()")
    @PutMapping("/update-doctor/{id}")
    public ResponseEntity<Doctor> updateEmployee(@PathVariable(value = "id") Long id,
                                                 @Validated @RequestBody Doctor doctorDetails) throws ResourceNotFoundException {
        Doctor doctorUser = doctorUserRepository.findById(id).get();
        doctorUser.setDateOfEmployment(doctorDetails.getDateOfEmployment());
        doctorUser.setEducation(doctorDetails.getEducation());
        doctorUser.setRoom(doctorDetails.getRoom());
        doctorUser.setSpecialist(doctorDetails.getSpecialist());

        doctorUser.getUser().setFirstName(doctorDetails.getUser().getFirstName());
        doctorUser.getUser().setLastName(doctorDetails.getUser().getLastName());
        final Doctor updatedEmployee = doctorUserRepository.save(doctorUser);

        //-----------
        return ResponseEntity.ok(updatedEmployee);
    }
}