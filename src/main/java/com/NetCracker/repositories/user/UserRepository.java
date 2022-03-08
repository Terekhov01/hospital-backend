package com.NetCracker.repositories.user;

import java.util.List;
import java.util.Optional;

import com.NetCracker.entities.user.role.ERole;
import com.NetCracker.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserName(String username);

//	@Query("FROM User user WHERE :role IN user.roles")
//	@Query("select All from User user WHERE :role in user.roles.name")
	@Query("FROM User user WHERE :role IN (user.roles)")
	List<User> findByRole(@Param("role") ERole role);

	Boolean existsByUserName(String username);

	Boolean existsByEmail(String email);
	User findByEmailIgnoreCase(String email);

}
