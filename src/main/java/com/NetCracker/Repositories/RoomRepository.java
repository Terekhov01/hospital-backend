package com.NetCracker.Repositories;

import com.NetCracker.Entities.Doctor.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room,Integer> {

    Optional<Room> findByNum(Integer num);
}
