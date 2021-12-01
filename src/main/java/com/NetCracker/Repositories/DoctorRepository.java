package com.NetCracker.Repositories;

import com.NetCracker.Entities.DoctorStub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DoctorRepository extends JpaRepository<DoctorStub, Long>
{
    interface DoctorShortInformation
    {
        Long getId();
        String getName();
        String getSpecialization();
        //TODO - add specialization
    }

    //findByIdInOrderBySpecializationAscNameAsc
    Collection<DoctorShortInformation> findByIdInOrderBySpecializationAscNameAsc(Collection<Long> doctorIds);
}
