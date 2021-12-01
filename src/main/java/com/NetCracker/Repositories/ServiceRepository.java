package com.NetCracker.Repositories;

import com.NetCracker.Entities.Doctor;
import com.NetCracker.Entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    @Query("select a from Service a order by a.id asc")
    List<Service> findAllByOrderByIdAsc();

    @Query("select a from Service a where a.id = :id")
    Optional<Service> findById(@Param("id") Long id);

    @Query("select a from Service a where a.serviceName = :name")
    Optional<Service> findServiceByName(@Param("name") String name);
}
