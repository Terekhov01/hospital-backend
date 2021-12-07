package com.NetCracker.Repositories;

import java.util.List;
import java.util.Optional;

import com.NetCracker.Entities.ERole;
import com.NetCracker.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	@Query("FROM User WHERE :role IN roles")
	List<User> findByRole(@Param("role") ERole role);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);
}
