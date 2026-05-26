package com.farmape.backend.productos.repository;

import com.farmape.backend.productos.enums.EstadoProducto;
import com.farmape.backend.productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByEstado(EstadoProducto estado);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}