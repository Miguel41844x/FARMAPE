package com.farmape.backend.formulas.service;

import com.farmape.backend.almacen.model.LoteProducto;
import com.farmape.backend.almacen.repository.LoteProductoRepository;
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
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FormulasService {

    private static final Set<String> ESTADOS_RECETA = Set.of(
            "Registrada", "Validada", "Presupuestada", "Aprobada", "En Elaboracion", "Preparada", "Lista", "Entregada", "Anulada"
    );

    private final RecetaMagistralRepository recetaMagistralRepository;
    private final FormulaMagistralRepository formulaMagistralRepository;
    private final DetalleFormulaMagistralRepository detalleFormulaMagistralRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final LoteProductoRepository loteProductoRepository;
    private final OrdenVentaRepository ordenVentaRepository;
    private final HistorialOrdenVentaRepository historialOrdenVentaRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public FormulasService(
            RecetaMagistralRepository recetaMagistralRepository,
            FormulaMagistralRepository formulaMagistralRepository,
            DetalleFormulaMagistralRepository detalleFormulaMagistralRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            LoteProductoRepository loteProductoRepository,
            OrdenVentaRepository ordenVentaRepository,
            HistorialOrdenVentaRepository historialOrdenVentaRepository,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.recetaMagistralRepository = recetaMagistralRepository;
        this.formulaMagistralRepository = formulaMagistralRepository;
        this.detalleFormulaMagistralRepository = detalleFormulaMagistralRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.loteProductoRepository = loteProductoRepository;
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
                .medicoPrescriptor(limpiar(request.medicoPrescriptor()))
                .numeroColegiatura(limpiar(request.numeroColegiatura()))
                .descripcionReceta(construirDescripcionReceta(
                        request.tipoFormula(),
                        request.diagnostico(),
                        request.componentes()
                ))
                .contraindicaciones(limpiar(request.contraindicaciones()))
                .presupuesto(null)
                .fechaReceta(LocalDateTime.now())
                .estado("Registrada")
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
                .medicoPrescriptor(limpiar(request.medicoPrescriptor()))
                .numeroColegiatura(limpiar(request.numeroColegiatura()))
                .descripcionReceta(construirDescripcionReceta(
                        request.tipoFormula(),
                        request.diagnostico(),
                        request.componentes()
                ))
                .contraindicaciones(limpiar(request.contraindicaciones()))
                .presupuesto(request.presupuesto())
                .fechaReceta(LocalDateTime.now())
                .estado("Aprobada")
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
                    .idLote(seleccionarLoteFefo(producto, insumoRequest.cantidad()).map(LoteProducto::getIdLote).orElse(null))
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
                "Fórmula magistral aprobada y orden enviada a caja."
        );
    }

    @Transactional
    public RecetaMagistralResponse cambiarEstado(Integer idReceta, CambiarEstadoRecetaRequest request) {
        RecetaMagistral receta = recetaMagistralRepository.findById(idReceta)
                .orElseThrow(() -> new RuntimeException("Receta magistral no encontrada"));

        String estadoNuevo = normalizarEstado(request.estado());
        receta.setEstado(estadoNuevo);

        Optional<FormulaMagistral> formulaOpt = formulaMagistralRepository.findByReceta(receta);
        formulaOpt.ifPresent(formula -> {
            if ("En Elaboracion".equals(estadoNuevo)) {
                formula.setEstado("Pendiente");
            } else if ("Preparada".equals(estadoNuevo) || "Lista".equals(estadoNuevo)) {
                formula.setEstado("Preparada");
                if (formula.getFechaElaboracion() == null) {
                    formula.setFechaElaboracion(LocalDateTime.now());
                }
            } else if ("Entregada".equals(estadoNuevo)) {
                formula.setEstado("Entregada");
                if (formula.getFechaElaboracion() == null) {
                    formula.setFechaElaboracion(LocalDateTime.now());
                }
            } else if ("Anulada".equals(estadoNuevo)) {
                formula.setEstado("Anulada");
            }
            formulaMagistralRepository.save(formula);
        });

        return toResponse(recetaMagistralRepository.save(receta));
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

    private Optional<LoteProducto> seleccionarLoteFefo(Producto producto, BigDecimal cantidad) {
        return loteProductoRepository.findDisponiblesForUpdateByProductoFefo(producto).stream()
                .filter(lote -> lote.getStockDisponible() != null
                        && BigDecimal.valueOf(lote.getStockDisponible()).compareTo(cantidad) >= 0)
                .findFirst();
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
        Optional<FormulaMagistral> formulaOpt = formulaMagistralRepository.findByReceta(receta);
        Integer idOrdenVenta = formulaOpt
                .map(formula -> formula.getOrdenVenta() != null ? formula.getOrdenVenta().getIdOrdenVenta() : null)
                .orElse(null);
        Integer idFormula = formulaOpt.map(FormulaMagistral::getIdFormula).orElse(null);
        String estadoFormula = formulaOpt.map(FormulaMagistral::getEstado).orElse(null);

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
                idOrdenVenta,
                idFormula,
                estadoFormula
        );
    }

    private String normalizarEstado(String estado) {
        String valor = valorO(estado, "Registrada").trim();
        String normalizado = switch (valor.toUpperCase(Locale.ROOT).replace("_", " ")) {
            case "REGISTRADA" -> "Registrada";
            case "VALIDADA" -> "Validada";
            case "PRESUPUESTADA" -> "Presupuestada";
            case "APROBADA" -> "Aprobada";
            case "EN ELABORACION", "EN ELABORACIÓN" -> "En Elaboracion";
            case "PREPARADA" -> "Preparada";
            case "LISTA" -> "Lista";
            case "ENTREGADA" -> "Entregada";
            case "ANULADA" -> "Anulada";
            default -> valor;
        };

        if (!ESTADOS_RECETA.contains(normalizado)) {
            throw new RuntimeException("Estado de receta no permitido: " + estado);
        }
        return normalizado;
    }

    private String valorO(String valor, String fallback) {
        return valor == null || valor.isBlank() ? fallback : valor;
    }

    private String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        return limpio.isBlank() ? null : limpio;
    }
}
