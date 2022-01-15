package com.NetCracker.controllers;


import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.doctor.DoctorRating;
import com.NetCracker.entities.doctor.Room;
import com.NetCracker.entities.doctor.Specialist;
import com.NetCracker.entities.schedule.DoctorSchedule;
import com.NetCracker.entities.user.ERole;
import com.NetCracker.entities.user.Role;
import com.NetCracker.repositories.RoomRepository;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.repositories.user.UserRepository;
import com.NetCracker.services.user.UserService;
import com.NetCracker.entities.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    // get all users
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/employees")
    public List<User> getAllUsers(){
        return  userService.findAll();
    }

    // create user rest api
    @PostMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(@RequestBody User user) {
        return  userService.saveUser(user);
    }

    // get user by id rest api

    @GetMapping("/employees/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user =  this.userService.findById(id);

        return ResponseEntity.ok().body(user);
    }

    // update user rest api
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/employees/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails){
        User user =  userService.findById(id);

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPatronymic(userDetails.getPatronymic());
        user.setPhone(userDetails.getPhone());
        user.setRoles(userDetails.getRoles());
        User updatedUser =  userService.saveUser(user);

        Set<Role> roles = new HashSet<Role>(user.getRoles());

        for(Role role: roles) {
            if (role.getName() == ERole.ROLE_DOCTOR) {
                Room room = roomRepository.getById(1);
                Doctor doc = new Doctor(new Date(), "education",
                        room, null, null, updatedUser.getFirstName(), updatedUser.getLastName(), null, updatedUser, updatedUser.getId());
                doctorRepository.save(doc);
            }
        }

        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id){
        User user =  userService.findById(id);

         userService.deleteById(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }


}