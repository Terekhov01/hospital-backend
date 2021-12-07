package Hospital.doctors.repository;

import Hospital.doctors.domain.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface AllUserRepository extends Repository<User,Integer> {

//    @Query(value = "SELECT AVG(r.rating ) FROM AllUsers r WHERE   ")
//    AllUsers findByRoleLike(@Param());
@Query(value = "SELECT a FROM User a WHERE a.role like '1'")
    List<User> findByRoleLike(String role);

//List<AllUsers> findAll();
    Optional<User> findById(int id);

    User save(User user);

}
