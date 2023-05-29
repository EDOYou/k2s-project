package com.edoyou.k2sbeauty.services.implementations;

import com.edoyou.k2sbeauty.entities.model.Role;
import com.edoyou.k2sbeauty.repositories.RoleRepository;
import com.edoyou.k2sbeauty.repositories.UserRepository;
import com.edoyou.k2sbeauty.services.interfaces.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service implementation for managing roles in the application. Provides methods for creating,
 * retrieving, updating, and deleting roles, as well as assigning roles to users.
 *
 * @author Taghiyev Kanan
 * @see Role
 * @see RoleRepository
 * @see UserRepository
 * @since 2023-05-28
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

}