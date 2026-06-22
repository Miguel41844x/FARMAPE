package com.farmape.backend.formulas.controller;

import com.farmape.backend.formulas.dto.*;
import com.farmape.backend.formulas.service.FormulasService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formulas")
public class FormulasController {

    private final FormulasService formulasService;

    public FormulasController(FormulasService formulasService) {
        this.formulasService = formulasService;
    }

    @GetMapping("/recetas-listas")
    public List<RecetaMagistralResponse> listarRecetas() {
        return formulasService.listarRecetas();
    }

    @GetMapping("/insumos")
    public List<InsumoFormulaResponse> listarInsumos() {
        return formulasService.listarInsumosDisponibles();
    }

    @PostMapping("/recetas")
    public RecetaMagistralResponse registrarReceta(@Valid @RequestBody RegistrarRecetaRequest request) {
        return formulasService.registrarReceta(request);
    }

    @PostMapping("/presupuestar")
    public PresupuestoFormulaResponse presupuestar(@Valid @RequestBody PresupuestarFormulaRequest request) {
        return formulasService.presupuestarFormula(request);
    }
}
