package com.farmape.backend.formulas.repository;

import com.farmape.backend.formulas.model.RecetaMagistral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecetaMagistralRepository extends JpaRepository<RecetaMagistral, Integer> {
    List<RecetaMagistral> findAllByOrderByFechaRecetaDesc();
}
