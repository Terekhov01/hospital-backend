package com.NetCracker.services.doctor;

import com.NetCracker.controllers.UserController;
import com.NetCracker.domain.DTO.DoctorUnionUserDto;
import com.NetCracker.domain.DTO.UserDto;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.doctor.Room;
import com.NetCracker.entities.doctor.Specialist;
import com.NetCracker.entities.user.ERole;
import com.NetCracker.entities.user.Role;
import com.NetCracker.entities.user.User;
import com.NetCracker.repositories.RoleRepository;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.repositories.RoomRepository;
import com.NetCracker.repositories.SpecialistRepository;
import com.NetCracker.repositories.user.UserRepository;
import com.NetCracker.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DoctorUserServiceImpl implements DoctorUserService {

    private final DoctorRepository doctorRepository;
    private final RoomRepository roomRepository;
    private final SpecialistRepository specialistRepository;

    private final UserRepository userRepository;
    private final UserService userService;



    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserController userController;

//    @PostConstruct
//    private void Getsmth(){
//
//        doctorRepository.names();
//        System.out.println();
//
//    }

    @Override
    public Doctor create(UserDto user) {

        if(user==null || user.getRoom() == null || user.getSpecialist()==null || user.getEducation()==null || user.getEducation().isEmpty()){
            return null;
        }
        Optional<Room> optionalRoom = roomRepository.findByNum(user.getRoom());
        List<Specialist> optionalSpecialist = specialistRepository.findBySpecializationIn(user.getSpecialist().stream().map(Specialist::getSpecialization).collect(Collectors.toList()));
        if(optionalRoom.isPresent() && !optionalSpecialist.isEmpty()){

            User simpleUser = new User();
            if(!user.getFirstname().isEmpty() && !user.getLastname().isEmpty()) {
                simpleUser.setFirstName(user.getFirstname());
                simpleUser.setLastName(user.getLastname());
                userService.saveUser(simpleUser);
                          }
// момент требующий внимания
//to do

            Doctor doctorUser = new Doctor();
            doctorUser.setId(simpleUser.getId());
//            doctorUser.getSpecialist().add(optionalSpecialist.get());
            doctorUser.addSpecialist(optionalSpecialist);
            doctorUser.setRoom(optionalRoom.get());
            doctorUser.setEducation(user.getEducation());
            doctorUser.setDateOfEmployment(new Date());

            doctorUser.setUser(userService.findById(simpleUser.getId()));

//            Role userrole = new Role();

            Set<Role> roles = new HashSet<>();
//            roles = "mod";
            Role modRole = roleRepository.findByName(ERole.ROLE_DOCTOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);

            simpleUser.setRoles(roles);


            return doctorRepository.save(doctorUser);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        Doctor doctorUser = findById(id);

        if (doctorUser != null) {
            doctorUser.getSpecialist().forEach(x-> x.getDoctors().remove(doctorUser));
            doctorUser.getSpecialist().clear();

            userService.deleteById(doctorUser.getId());

            doctorRepository.delete(doctorUser);
        }
    }

    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    @Override
    public List<DoctorUnionUserDto> findAllDoctorWithName() {
        return doctorRepository.findAll().stream()
                .map(doctor->{
                    User byIdUser = userService.findById(doctor.getId());
                    return new DoctorUnionUserDto(doctor,byIdUser);
                })
                .collect(Collectors.toList());
    }
//    @Override
//    public Doctor findById(Long id) {
//
//       if (id != null) {
//           User simpleUser = new User();
//
////           Doctor doctor = findById(id);
//////           doctorRepository.names().get(id)= simpleUser.getFirstName();
////           simpleUser.getLastName();
////
////           simpleUser.getFirstName();
//
////           return userService.findById(id);
//
////           return simpleUser;
////           return simpleUser;
//
//        }
//
//        return doctorRepository.findById(id).get();
//    }

    @Override
    //Doctor
    public Doctor findById(Long id) {
        return doctorRepository.findById(id).get();
    }

//    @Override
//    public DoctorUnionUserDto findedById(Long id) {
//        return null;
//    }

    @Override
    public DoctorUnionUserDto findDoctorWithName(Long id){

        Optional<Doctor> byId = doctorRepository.findById(id);
        User byId1 = userService.findById(id);
        return byId.map(x->new DoctorUnionUserDto(x,byId1)).orElse(null);
    }

    @Override
    public Doctor findByRelatedUserId(Long userId)
    {
        return doctorRepository.findByUser_id(userId).orElse(null);
    }

    @Override
    @Transactional
    public DoctorUnionUserDto update(DoctorUnionUserDto doctor) {
        Optional<Room> optionalRoom = roomRepository.findByNum(doctor.getRoom().getNum());
        List<Specialist> optionalSpec = specialistRepository.findBySpecializationIn(doctor.getSpecialist().stream().map(x->x.getSpecialization()).collect(Collectors.toList()));

        Doctor byId = doctorRepository.findById(doctor.getId()).get();
        BeanUtils.copyProperties(doctor,byId,"id","dateOfEmployment");

        optionalRoom.ifPresent(x-> byId.setRoom(x));
        byId.getSpecialist().clear();
        byId.getSpecialist().addAll(optionalSpec);

        User user = userService.findById(byId.getId());

        if(StringUtils.hasText(doctor.getFirstname())) {

            user.setFirstName(doctor.getFirstname());
        }
        if(StringUtils.hasText(doctor.getLastname())) {
            user.setLastName(doctor.getLastname());
        }
        byId.setUser(user);
        User saveUser = userService.saveUser(user);

        Doctor doctorSave = doctorRepository.save(byId);
//       userController.updateUser(byId.getId());
//
        return new DoctorUnionUserDto(doctorSave,saveUser);
    }
}
