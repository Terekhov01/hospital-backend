package com.NetCracker.Services;

import com.NetCracker.Entities.DoctorStub;
import com.NetCracker.Repositories.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorStubService
{
    @Autowired
    DoctorRepository doctorRepository;

    @Transactional
    public DoctorStub getDoctorById(long id)
    {
        return doctorRepository.findById(id).orElse(null);
    }
}
