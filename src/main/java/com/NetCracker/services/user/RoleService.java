package com.NetCracker.services.user;

import com.NetCracker.entities.user.role.ERole;
import com.NetCracker.entities.user.role.Role;
import com.NetCracker.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService
{
    @Autowired
    RoleRepository roleRepository;

    public Role findByName(ERole eRole)
    {
        return roleRepository.findByName(eRole).orElse(null);
    }
}
