package Hospital.doctors.service;

import Hospital.doctors.domain.entity.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AllUserService {

    List<User> findByRoleLike(String role);
//    List<AllUsers> findAll();
    User findById(int id);

    @Query(nativeQuery = true, value = "SELECT u.id,u.firstname,u.lastname,AVG(z.rate) AS rating FROM users u INNER JOIN users_rating z  on u.id = z.users_id GROUP BY u.id, u.firstname, u.lastname")
    List<User> findAll();
}