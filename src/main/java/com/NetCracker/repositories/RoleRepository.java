package com.NetCracker.repositories;

import java.util.Optional;

import com.NetCracker.entities.user.role.ERole;
import com.NetCracker.entities.user.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
}
