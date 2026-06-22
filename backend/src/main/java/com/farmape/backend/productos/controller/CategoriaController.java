package com.farmape.backend.productos.controller;

import com.farmape.backend.productos.repository.CategoriaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll().stream()
                .map(categoria -> new CategoriaResponse(
                        categoria.getIdCategoria(),
                        categoria.getNombre(),
                        categoria.getDescripcion()
                ))
                .toList();
    }

    public record CategoriaResponse(Integer idCategoria, String nombre, String descripcion) {
    }
}
