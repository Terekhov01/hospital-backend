package Hospital.doctors.controller;


import Hospital.doctors.domain.dto.DoctorRatingProjection;
import Hospital.doctors.domain.entity.Doctor;
import Hospital.doctors.exception.ResourceNotFoundException;
import Hospital.doctors.repository.DoctorUserRepository;
import Hospital.doctors.service.DoctorUserService;
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
    private DoctorUserRepository doctorUserRepository;
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
    public Doctor findOne(@PathVariable("id") int id){
        return doctorUserService.findById(id);
    }

}
