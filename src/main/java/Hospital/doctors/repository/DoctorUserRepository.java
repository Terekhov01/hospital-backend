package Hospital.doctors.repository;

import Hospital.doctors.domain.entity.Doctor;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface DoctorUserRepository extends Repository<Doctor, Integer> {

    void delete(Doctor user);

    List<Doctor> findAll();

    Optional<Doctor> findById(int id);

    Doctor save(Doctor user);
}
