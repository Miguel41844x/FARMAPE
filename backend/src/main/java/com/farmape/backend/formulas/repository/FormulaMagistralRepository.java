package com.farmape.backend.formulas.repository;

import com.farmape.backend.formulas.model.FormulaMagistral;
import com.farmape.backend.formulas.model.RecetaMagistral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FormulaMagistralRepository extends JpaRepository<FormulaMagistral, Integer> {
    Optional<FormulaMagistral> findByReceta(RecetaMagistral receta);
}
