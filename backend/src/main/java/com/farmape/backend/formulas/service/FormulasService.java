package com.farmape.backend.formulas.service;

import com.farmape.backend.clientes.model.Cliente;
import com.farmape.backend.clientes.repository.ClienteRepository;
import com.farmape.backend.formulas.dto.*;
import com.farmape.backend.formulas.model.DetalleFormulaMagistral;
import com.farmape.backend.formulas.model.FormulaMagistral;
import com.farmape.backend.formulas.model.RecetaMagistral;
import com.farmape.backend.formulas.repository.DetalleFormulaMagistralRepository;
import com.farmape.backend.formulas.repository.FormulaMagistralRepository;
import com.farmape.backend.formulas.repository.RecetaMagistralRepository;
import com.farmape.backend.productos.enums.EstadoProducto;
import com.farmape.backend.productos.model.Producto;
import com.farmape.backend.productos.repository.ProductoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.ventas.enums.CanalPedido;
import com.farmape.backend.ventas.enums.EstadoOrdenVenta;
import com.farmape.backend.ventas.model.HistorialOrdenVenta;
import com.farmape.backend.ventas.model.OrdenVenta;
import com.farmape.backend.ventas.repository.HistorialOrdenVentaRepository;
import com.farmape.backend.ventas.repository.OrdenVentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FormulasService {

    private final RecetaMagistralRepository recetaMagistralRepository;
    private final FormulaMagistralRepository formulaMagistralRepository;
    private final DetalleFormulaMagistralRepository detalleFormulaMagistralRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final OrdenVentaRepository ordenVentaRepository;
    private final HistorialOrdenVentaRepository historialOrdenVentaRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public FormulasService(
            RecetaMagistralRepository recetaMagistralRepository,
            FormulaMagistralRepository formulaMagistralRepository,
            DetalleFormulaMagistralRepository detalleFormulaMagistralRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            OrdenVentaRepository ordenVentaRepository,
            HistorialOrdenVentaRepository historialOrdenVentaRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.recetaMagistralRepository = recetaMagistralRepository;
        this.formulaMagistralRepository = formulaMagistralRepository;
        this.detalleFormulaMagistralRepository = detalleFormulaMagistralRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.ordenVentaRepository = ordenVentaRepository;
        this.historialOrdenVentaRepository = historialOrdenVentaRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    public List<RecetaMagistralResponse> listarRecetas() {
        return recetaMagistralRepository.findAllByOrderByFechaRecetaDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<InsumoFormulaResponse> listarInsumosDisponibles() {
        return productoRepository.findByEstado(EstadoProducto.Activo).stream()
                .filter(producto -> producto.getStockActual() != null && producto.getStockActual() > 0)
                .map(producto -> new InsumoFormulaResponse(
                        producto.getIdProducto(),
                        producto.getNombre(),
                        producto.getDescripcion(),
                        producto.getLaboratorio(),
                        producto.getPrecioCompra(),
                        producto.getPrecioVenta(),
                        producto.getStockActual(),
                        producto.getEstado() != null ? producto.getEstado().name() : null
                ))
                .toList();
    }

    @Transactional
    public RecetaMagistralResponse registrarReceta(RegistrarRecetaRequest request) {
        Cliente cliente = obtenerCliente(request.dniPaciente());
        Trabajador quimico = authenticatedUserService.currentAccount().getTrabajador();

        RecetaMagistral receta = RecetaMagistral.builder()
                .cliente(cliente)
                .quimicoFarmaceutico(quimico)
                .medicoPrescriptor(request.medicoPrescriptor())
                .numeroColegiatura(request.numeroColegiatura())
                .descripcionReceta(construirDescripcionReceta(
                        request.tipoFormula(),
                        request.diagnostico(),
                        request.componentes()
                ))
                .contraindicaciones(request.contraindicaciones())
                .presupuesto(null)
                .fechaReceta(LocalDateTime.now())
                .estado("Presupuestada")
                .build();

        return toResponse(recetaMagistralRepository.save(receta));
    }

    @Transactional
    public PresupuestoFormulaResponse presupuestarFormula(PresupuestarFormulaRequest request) {
        Cliente cliente = obtenerCliente(request.dniPaciente());
        Trabajador quimico = authenticatedUserService.currentAccount().getTrabajador();

        validarInsumos(request.insumos());

        RecetaMagistral receta = RecetaMagistral.builder()
                .cliente(cliente)
                .quimicoFarmaceutico(quimico)
                .medicoPrescriptor(request.medicoPrescriptor())
                .numeroColegiatura(request.numeroColegiatura())
                .descripcionReceta(construirDescripcionReceta(
                        request.tipoFormula(),
                        request.diagnostico(),
                        request.componentes()
                ))
                .contraindicaciones(request.contraindicaciones())
                .presupuesto(request.presupuesto())
                .fechaReceta(LocalDateTime.now())
                .estado("Aceptada")
                .build();
        receta = recetaMagistralRepository.save(receta);

        OrdenVenta orden = OrdenVenta.builder()
                .cliente(cliente)
                .empleado(quimico)
                .canalPedido(CanalPedido.Presencial)
                .estado(EstadoOrdenVenta.Confirmada)
                .fechaOrden(LocalDateTime.now())
                .total(request.presupuesto())
                .observacion("Orden de venta generada por fórmula magistral #" + receta.getIdReceta())
                .build();
        orden = ordenVentaRepository.save(orden);

        historialOrdenVentaRepository.save(HistorialOrdenVenta.builder()
                .ordenVenta(orden)
                .trabajador(quimico)
                .estadoAnterior(null)
                .estadoNuevo(EstadoOrdenVenta.Confirmada)
                .observacion("Orden confirmada automáticamente desde receta magistral")
                .fechaCambio(LocalDateTime.now())
                .build());

        FormulaMagistral formula = FormulaMagistral.builder()
                .receta(receta)
                .ordenVenta(orden)
                .fechaElaboracion(null)
                .descripcionFormula(valorO(request.descripcionFormula(), request.tipoFormula()))
                .instruccionesUso(valorO(request.instruccionesUso(), "Usar según indicación médica."))
                .estado("Pendiente")
                .build();
        formula = formulaMagistralRepository.save(formula);

        for (InsumoFormulaRequest insumoRequest : request.insumos()) {
            Producto producto = productoRepository.findById(insumoRequest.idProducto())
                    .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

            DetalleFormulaMagistral detalle = DetalleFormulaMagistral.builder()
                    .formula(formula)
                    .producto(producto)
                    .idLote(null)
                    .cantidadUsada(insumoRequest.cantidad())
                    .unidadMedida(valorO(insumoRequest.unidadMedida(), "unidad"))
                    .build();
            detalleFormulaMagistralRepository.save(detalle);
        }

        return new PresupuestoFormulaResponse(
                true,
                receta.getIdReceta(),
                formula.getIdFormula(),
                orden.getIdOrdenVenta(),
                receta.getPresupuesto(),
                receta.getEstado(),
                "Fórmula magistral presupuestada y orden enviada a caja."
        );
    }

    private Cliente obtenerCliente(String dniPaciente) {
        return clienteRepository.findByDniRuc(dniPaciente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado. Registre al cliente antes de crear la receta."));
    }

    private void validarInsumos(List<InsumoFormulaRequest> insumos) {
        for (InsumoFormulaRequest insumoRequest : insumos) {
            Producto producto = productoRepository.findById(insumoRequest.idProducto())
                    .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

            if (producto.getEstado() != EstadoProducto.Activo) {
                throw new RuntimeException("El insumo " + producto.getNombre() + " no está activo");
            }

            if (producto.getStockActual() == null || BigDecimal.valueOf(producto.getStockActual()).compareTo(insumoRequest.cantidad()) < 0) {
                throw new RuntimeException("Stock insuficiente para el insumo: " + producto.getNombre());
            }
        }
    }

    private String construirDescripcionReceta(
            String tipoFormula,
            String diagnostico,
            List<ComponenteRecetaRequest> componentes
    ) {
        String detalleComponentes = componentes == null || componentes.isEmpty()
                ? "Sin componentes declarados"
                : componentes.stream()
                        .filter(Objects::nonNull)
                        .map(c -> c.nombre_insumo() + " (" + c.cantidad_usada() + " " + c.unidad_medida() + ")")
                        .collect(Collectors.joining(", "));

        return valorO(tipoFormula, "Fórmula magistral")
                + " | Diagnóstico: " + valorO(diagnostico, "No especificado")
                + " | Componentes prescritos: " + detalleComponentes;
    }

    private RecetaMagistralResponse toResponse(RecetaMagistral receta) {
        Integer idOrdenVenta = formulaMagistralRepository.findByReceta(receta)
                .map(formula -> formula.getOrdenVenta() != null ? formula.getOrdenVenta().getIdOrdenVenta() : null)
                .orElse(null);

        return new RecetaMagistralResponse(
                receta.getIdReceta(),
                receta.getCliente().getIdCliente(),
                receta.getCliente().getDniRuc(),
                receta.getCliente().getNombres() + " " + valorO(receta.getCliente().getApellidos(), ""),
                receta.getQuimicoFarmaceutico().getIdTrabajador(),
                receta.getQuimicoFarmaceutico().getNombres() + " " + receta.getQuimicoFarmaceutico().getApellidos(),
                receta.getMedicoPrescriptor(),
                receta.getNumeroColegiatura(),
                receta.getDescripcionReceta(),
                receta.getContraindicaciones(),
                receta.getPresupuesto(),
                receta.getFechaReceta(),
                receta.getEstado(),
                idOrdenVenta
        );
    }

    private String valorO(String valor, String fallback) {
        return valor == null || valor.isBlank() ? fallback : valor;
    }
}
