package com.farmape.ms.inventario.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.farmape.ms.inventario.domain.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByActivoTrueOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);
}
