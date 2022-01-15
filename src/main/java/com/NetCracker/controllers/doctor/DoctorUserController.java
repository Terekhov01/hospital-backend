package com.NetCracker.controllers.doctor;


import com.NetCracker.domain.DTO.UserDto;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.controllers.exception.ResourceNotFoundException;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.services.doctor.DoctorUserService;
import com.NetCracker.services.doctor.DoctorUserServiceImpl;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//import javax.validation.Valid;
import java.util.List;
@AllArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
//@CrossOrigin(origins = "http://localhost:4200")
//@RequestMapping({"/api"})
@RequestMapping({"/doctorusers"})
public class DoctorUserController {

    @Autowired
    private DoctorUserService doctorUserService;

    @Autowired
    private DoctorRepository doctorUserRepository;

    @PostMapping
    public Doctor create(@RequestBody UserDto user){
        return doctorUserService.create(user);
    }

    @GetMapping(path = {"/{id}"})
    public Doctor findOne(@PathVariable("id") long id){
        return doctorUserService.findById(id);
    }

    @PutMapping(path = {"/{id}"})
    public Doctor update( @RequestBody Doctor user){
        return doctorUserService.update(user);
    }

    @DeleteMapping(path ={"/{id}"})
    public void delete(@PathVariable("id") Long id) {
        doctorUserService.delete(id);
    }

    @GetMapping
    public List<Doctor> findAll(){
        return doctorUserService.findAll();
    }

    @GetMapping("/update-doctor/{id}")
    public ResponseEntity<Doctor> getEmployeeById(@PathVariable Long id) throws ResourceNotFoundException {
        Doctor doctorUser = doctorUserRepository.findById(id).get();
        return ResponseEntity.ok(doctorUser);
    }

    @PutMapping("/update-doctor/{id}")
    public ResponseEntity<Doctor> updateEmployee(@PathVariable(value = "id") Long id,
                                                 @Validated @RequestBody Doctor doctorDetails) throws ResourceNotFoundException {
        Doctor doctorUser = doctorUserRepository.findById(id).get();
        doctorUser.setDateOfEmployment(doctorDetails.getDateOfEmployment());
        doctorUser.setEducation(doctorDetails.getEducation());
        doctorUser.setRoom(doctorDetails.getRoom());
        doctorUser.setSpecialist(doctorDetails.getSpecialist());
        final Doctor updatedEmployee = doctorUserRepository.save(doctorUser);
        return ResponseEntity.ok(updatedEmployee);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("find-by/specialization")
    public ResponseEntity<String> getDoctorBySpecialization(@RequestParam(value = "specialization") String specializationName)
    {
        List<DoctorUserService.DoctorShortInfo> doctorShortInfos;
        try
        {
            doctorShortInfos = doctorUserService.findShortInfoBySpecializationName(specializationName);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("Server could not connect to database", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Gson gson = new Gson();

        String doctorShortInfosStr = gson.toJson(doctorShortInfos);

        return new ResponseEntity<String>(doctorShortInfosStr, HttpStatus.OK);
    }
}