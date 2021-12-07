package Hospital.doctors.repository;

import Hospital.doctors.domain.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Integer> {

    Optional<Room> findByNum(Integer num);
}
