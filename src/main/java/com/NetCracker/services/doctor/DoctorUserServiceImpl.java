package com.NetCracker.services.doctor;

import com.NetCracker.controllers.UserController;
import com.NetCracker.domain.DTO.DoctorUnionUserDto;
import com.NetCracker.domain.DTO.UserDto;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.doctor.Room;
import com.NetCracker.entities.doctor.Specialist;
import com.NetCracker.entities.user.role.ERole;
import com.NetCracker.entities.user.role.Role;
import com.NetCracker.entities.user.User;
import com.NetCracker.repositories.RoleRepository;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.repositories.RoomRepository;
import com.NetCracker.repositories.doctor.SpecialistRepository;
import com.NetCracker.repositories.user.UserRepository;
import com.NetCracker.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    @Transactional
    public void save(Doctor doctor)
    {
        this.doctorRepository.save(doctor);
    }

    @Override
    public void delete(Long id) {
        Doctor doctorUser = findById(id);

        if (doctorUser != null) {
            doctorUser.getSpecialist().forEach(x-> x.getDoctors().remove(doctorUser));
            doctorUser.getSpecialist().clear();

            userService.deleteById(doctorUser.getId());

//            doctorRepository.delete(doctorUser);
        }
    }

    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    @Override
    public List<DoctorUnionUserDto> findAllDoctorWithName() {
        return doctorRepository.findAll().stream()
                .map(DoctorUnionUserDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public Long countAll()
    {
        return doctorRepository.count();
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
        return doctorRepository.findById(id).orElse(null);
    }

    @Override
    public Doctor findByRelatedUserId(Long userId)
    {
        return doctorRepository.findByUser_id(userId).orElse(null);
    }

    @Override
    public List<DoctorShortInfo> findShortInfoBySpecializationName(String specializationName) throws DataAccessException
    {
        var doctors = doctorRepository.findBySpecializationName(specializationName);

        var retVal = new ArrayList<DoctorShortInfo>();
        for (var doctor : doctors)
        {
            retVal.add(new DoctorShortInfo(doctor.getId(), doctor.getUser().getFirstName(), doctor.getUser().getLastName(), doctor.getUser().getPatronymic()));
        }

        return retVal;
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
        userService.saveUser(user);

        Doctor doctorSave = doctorRepository.save(byId);
//       userController.updateUser(byId.getId());
//
        return new DoctorUnionUserDto(doctorSave);
    }
}
