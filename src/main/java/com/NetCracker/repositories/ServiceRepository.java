package com.NetCracker.repositories;

import com.NetCracker.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    @Query("select a from Service a order by a.id asc")
    List<Service> findAllByOrderByIdAsc();

    @Query("select a from Service a where a.id = :id")
    Optional<Service> findById(@Param("id") Long id);

    @Query("select a from Service a where a.serviceName = :name")
    Optional<Service> findServiceByName(@Param("name") String name);
}
