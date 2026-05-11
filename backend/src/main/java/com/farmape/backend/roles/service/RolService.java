package com.farmape.backend.roles.service;

import org.springframework.stereotype.Service;

import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.roles.repository.RolRepository;

import java.util.List;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }
}