package com.NetCracker.services.doctor;

import com.NetCracker.entities.doctor.Room;
import com.NetCracker.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService
{
    @Autowired
    RoomRepository roomRepository;

    public void save(Room room)
    {
        roomRepository.save(room);
    }

    public Room findByNum(Integer roomNumber)
    {
        return roomRepository.findByNum(roomNumber).orElse(null);
    }
}
