package com.NetCracker.controllers;


import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.doctor.Room;
import com.NetCracker.entities.patient.Patient;
import com.NetCracker.entities.user.role.ERole;
import com.NetCracker.entities.user.role.Role;
import com.NetCracker.payload.Request.PatientDTO;
import com.NetCracker.payload.Request.UserDTO;
import com.NetCracker.payload.Response.UserPersonalAccountDTO;
import com.NetCracker.repositories.RoomRepository;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.repositories.user.UserRepository;
import com.NetCracker.services.PatientService;
import com.NetCracker.services.security.AuthenticationService;
import com.NetCracker.services.user.UserService;
import com.NetCracker.entities.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/")
@RestController
public class UserController {
    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    // get all users
    @GetMapping("/employees")
    public List<User> getAllUsers(){
        return  userService.findAll();
    }

    // create user rest api
    @PostMapping("/employees")
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // get user by id rest api
    @GetMapping("/employees/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user =  this.userService.findById(id);

        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_USER') || hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO, Authentication authentication)
    {
        User authenticatedUser;
        try
        {
            authenticatedUser = authenticationService.getAuthenticatedUser(authentication);
        }
        catch (ClassCastException e)
        {
            return ResponseEntity.internalServerError().body("???? ?????????????? ?????????????????????? ?????? ??????????????");
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("???? ?????????????? ?????????????????? ?? ?????????? ???????????? ?????? ?????????????????????????? ????????????????",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
        if (authenticatedUser == null)
        {
            return ResponseEntity.internalServerError().body("???? ?????????????? ?????????????????????? ?????? ??????????????");
        }

        User user = userService.findById(authenticatedUser.getId());

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPatronymic(userDTO.getMiddleName());
        user.setPhone(userDTO.getPhone());
        userService.saveUser(user);

        return ResponseEntity.ok("");
    }

    // update user rest api
    @PutMapping("/employees/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody PatientDTO patientSignupRequest){
        User user =  userService.findById(id);
        Set<Role> roles = new HashSet<Role>(user.getRoles());

        user.setFirstName(patientSignupRequest.getFirstName());
        user.setLastName(patientSignupRequest.getLastName());
        user.setPatronymic(patientSignupRequest.getMiddleName());
        user.setPhone(patientSignupRequest.getPhone());
        for(Role role: roles) {
            if (role.getName() == ERole.ROLE_PATIENT) {
                Patient patient = patientService.findById(id);
                patient.setPassport(patientSignupRequest.getPassport());
                patient.setPolys(patientSignupRequest.getPolys());
                patient.setUser(user);
                patientService.savePatient(patient);
            }
        }
        User updatedUser =  userService.saveUser(user);

//        for(Role role: roles) {
//            if (role.getName() == ERole.ROLE_DOCTOR) {
//                Room room = roomRepository.getById(1);
//                Doctor doc = new Doctor(new Date(), "education",
//                        room, null, null, updatedUser.getFirstName(), updatedUser.getLastName(), null, updatedUser, updatedUser.getId());
//                doctorRepository.save(doc);
//            }
//        }

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

    @PreAuthorize("hasRole('ROLE_USER') || hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<String> getDoctorAccountInfoById(@PathVariable Long id, Authentication authentication) {

        User authenticatedAdmin = null;
        try
        {
            authenticatedAdmin = authenticationService.getAuthenticatedAdmin(authentication);
        }
        catch (ClassCastException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????", HttpStatus.UNAUTHORIZED);
        }
        catch (DataAccessException e)
        {
            return new ResponseEntity<String>("???????????????????????????? ???? ????????????????. ???????????? ?????????????? ?????????????????? ?? ????",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (authenticatedAdmin == null)
        {
            return new ResponseEntity<String>("???? ???? ?????????????????? ??????????????????????????????", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        var userPersonalAccountDTO = new UserPersonalAccountDTO(authenticatedAdmin);
        String userPersonalAccountStr;
        try
        {
            userPersonalAccountStr = objectMapper.writeValueAsString(userPersonalAccountDTO);
        }
        catch (JsonProcessingException e)
        {
            return ResponseEntity.internalServerError().body("???????????? ???? ?????????????? - ???? ?????????????? ?????????????????????????? " +
                    "???????????????????? ?? ????????????");
        }

        return ResponseEntity.ok().body(userPersonalAccountStr);
    }
}
