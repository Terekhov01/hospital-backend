package com.NetCracker.controllers;


import com.NetCracker.services.user.UserService;
import com.NetCracker.entities.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

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