package Hospital.doctors.service;

import Hospital.doctors.domain.entity.User;
import Hospital.doctors.repository.AllUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllUsersServiceImpl implements AllUserService {


    @Autowired
    private AllUserRepository allUserRepository;

    @Override
    public List<User> findByRoleLike(String role) {
        return allUserRepository.findByRoleLike(role);
    }

    @Override
    public User findById(int id) {
        return allUserRepository.findById(id).get();
    }

    //заглушка
    @Override
    public List<User> findAll() {
        return null;
    }

}
