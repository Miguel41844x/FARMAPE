package com.farmape.backend.formulas.repository;

import com.farmape.backend.formulas.model.DetalleFormulaMagistral;
import com.farmape.backend.formulas.model.FormulaMagistral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleFormulaMagistralRepository extends JpaRepository<DetalleFormulaMagistral, Integer> {
    List<DetalleFormulaMagistral> findByFormula(FormulaMagistral formula);
}
