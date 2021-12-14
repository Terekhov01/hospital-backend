package com.NetCracker.services.doctor;

import com.NetCracker.domain.DTO.UserDto;
import com.NetCracker.entities.doctor.Doctor;
import com.NetCracker.entities.doctor.Room;
import com.NetCracker.entities.doctor.Specialist;
import com.NetCracker.repositories.doctor.DoctorRepository;
import com.NetCracker.repositories.RoomRepository;
import com.NetCracker.repositories.SpecialistRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@AllArgsConstructor
@Service
public class DoctorUserServiceImpl implements DoctorUserService {

    private final DoctorRepository doctorRepository;
    private final RoomRepository roomRepository;
    private final SpecialistRepository specialistRepository;

    @Override
    public Doctor create(UserDto user) {
        if(user==null || user.getRoom() == null || user.getSpecialist()==null || user.getEducation()==null || user.getEducation().isEmpty()){
            return null;
        }
        Optional<Room> optionalRoom = roomRepository.findByNum(user.getRoom());
        List<Specialist> optionalSpecialist = specialistRepository.findBySpecializationIn(user.getSpecialist().stream().map(Specialist::getSpecialization).collect(Collectors.toList()));
        if(optionalRoom.isPresent() && !optionalSpecialist.isEmpty()){
            Doctor doctorUser = new Doctor();
//            doctorUser.getSpecialist().add(optionalSpecialist.get());
            doctorUser.addSpecialist(optionalSpecialist);
            doctorUser.setRoom(optionalRoom.get());
            doctorUser.setEducation(user.getEducation());
            doctorUser.setDateOfEmployment(new Date());
            return doctorRepository.save(doctorUser);
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        Doctor user = findById(id);

        if (user != null) {
            user.getSpecialist().forEach(x-> x.getDoctors().remove(user));
            user.getSpecialist().clear();
            doctorRepository.delete(user);
        }
    }

    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor findById(Long id) {
        return doctorRepository.findById(id).get();
    }

    @Override
    public Doctor findByRelatedUserId(Long userId)
    {
        return doctorRepository.findByUser_id(userId).orElse(null);
    }

    @Override
    @Transactional
    public Doctor update(Doctor doctor) {
        Optional<Room> optionalRoom = roomRepository.findByNum(doctor.getRoom().getNum());
        List<Specialist> optionalSpec = specialistRepository.findBySpecializationIn(doctor.getSpecialist().stream().map(x->x.getSpecialization()).collect(Collectors.toList()));

        Doctor byId = doctorRepository.findById(doctor.getId()).get();
        BeanUtils.copyProperties(doctor,byId,"id","dateOfEmployment");

        optionalRoom.ifPresent(x-> byId.setRoom(x));
        byId.getSpecialist().clear();
        byId.getSpecialist().addAll(optionalSpec);
        return doctorRepository.save(byId);
    }
}
