package Hospital.doctors.service;

import Hospital.doctors.domain.dto.UserDto;
import Hospital.doctors.domain.entity.Doctor;
import Hospital.doctors.domain.entity.Room;
import Hospital.doctors.domain.entity.Specialist;
import Hospital.doctors.repository.DoctorUserRepository;
import Hospital.doctors.repository.RoomRepository;
import Hospital.doctors.repository.SpecialistRepository;
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

    private final DoctorUserRepository doctorUserRepository;
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

            doctorUser.setFirstname(user.getFirstname());
            doctorUser.setLastname(user.getLastname());
            //-----
            return doctorUserRepository.save(doctorUser);
        }
        return null;
    }

    @Override
    public void delete(Integer id) {
        Doctor user = findById(id);

        if (user != null) {
            user.getSpecialist().forEach(x-> x.getDoctors().remove(user));
            user.getSpecialist().clear();
            doctorUserRepository.delete(user);
        }
    }

    @Override
    public List<Doctor> findAll() {
        return doctorUserRepository.findAll();
    }

    @Override
    public Doctor findById(int id) {
        return doctorUserRepository.findById(id).get();
    }

    @Override
    @Transactional
    public Doctor update(Doctor user) {
        Optional<Room> optionalRoom = roomRepository.findByNum(user.getRoom().getNum());
        List<Specialist> optionalSpec = specialistRepository.findBySpecializationIn(user.getSpecialist().stream().map(x->x.getSpecialization()).collect(Collectors.toList()));

        Doctor byId = doctorUserRepository.findById(user.getId()).get();
        BeanUtils.copyProperties(user,byId,"id","dateOfEmployment");

        optionalRoom.ifPresent(x-> byId.setRoom(x));
        byId.getSpecialist().clear();
        byId.getSpecialist().addAll(optionalSpec);
        return doctorUserRepository.save(byId);
    }
}
