package Hospital.doctors.repository;

import Hospital.doctors.domain.entity.Room;
import Hospital.doctors.domain.entity.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface SpecialistRepository extends JpaRepository<Specialist,Integer> {

    List<Specialist> findBySpecializationIn(List<String> specialization);
}
