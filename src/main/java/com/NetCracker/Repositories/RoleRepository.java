package com.NetCracker.Repositories;

import java.util.List;
import java.util.Optional;

import com.NetCracker.Entities.ERole;
import com.NetCracker.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
}
