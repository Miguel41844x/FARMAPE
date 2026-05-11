package com.farmape.backend.roles.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmape.backend.roles.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Integer> {
}