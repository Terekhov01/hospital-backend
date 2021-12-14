package com.NetCracker.services;

import com.NetCracker.entities.doctor.UserStub;
import com.NetCracker.repositories.AllUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllUsersServiceImpl implements AllUserService {


    @Autowired
    private AllUserRepository allUserRepository;

    @Override
    public List<UserStub> findByRoleLike(String role) {
        return allUserRepository.findByRoleLike(role);
    }

    @Override
    public UserStub findById(int id) {
        return allUserRepository.findById(id).get();
    }

    //заглушка
    @Override
    public List<UserStub> findAll() {
        return null;
    }

}
