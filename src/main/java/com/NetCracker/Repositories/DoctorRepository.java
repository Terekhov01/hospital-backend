package com.NetCracker.Repositories;

import com.NetCracker.Entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>
{
    interface DoctorShortInformation
    {
        Long getId();
        String getName();
        //TODO - add specialization
    }

    Collection<DoctorShortInformation> findByIdIn(Collection<Long> doctorIds);
}
