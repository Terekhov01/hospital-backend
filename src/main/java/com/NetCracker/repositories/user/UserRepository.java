package com.NetCracker.repositories.user;

import java.util.List;
import java.util.Optional;

import com.NetCracker.entities.user.ERole;
import com.NetCracker.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserName(String username);

	@Query("FROM User WHERE :role IN roles")
	List<User> findByRole(@Param("role") ERole role);

	Boolean existsByUserName(String username);

	Boolean existsByEmail(String email);
}
