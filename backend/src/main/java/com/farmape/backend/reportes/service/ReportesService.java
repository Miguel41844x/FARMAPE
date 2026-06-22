package com.farmape.backend.reportes.service;

import com.farmape.backend.reportes.dto.AccionGerenciaResponse;
import com.farmape.backend.reportes.dto.ActualizarEstadoAccionRequest;
import com.farmape.backend.reportes.dto.InformeResponse;
import com.farmape.backend.reportes.dto.KpiResponse;
import com.farmape.backend.reportes.dto.RegistrarAccionRequest;
import com.farmape.backend.reportes.dto.RegistrarInformeRequest;
import com.farmape.backend.reportes.dto.ReporteResumenResponse;
import com.farmape.backend.reportes.dto.SerieItemResponse;
import com.farmape.backend.reportes.dto.StockCriticoResponse;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.trabajadores.model.Trabajador;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReportesService {

    private final JdbcTemplate jdbcTemplate;
    private final AuthenticatedUserService authenticatedUserService;

    public ReportesService(JdbcTemplate jdbcTemplate,
                           AuthenticatedUserService authenticatedUserService) {
        this.jdbcTemplate = jdbcTemplate;
        this.authenticatedUserService = authenticatedUserService;
    }

    public ReporteResumenResponse obtenerResumen() {
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        List<KpiResponse> kpis = List.of(
                new KpiResponse(
                        "VENTAS_TOTALES",
                        "Ventas acumuladas",
                        bigDecimal("""
                                SELECT COALESCE(SUM(total), 0)
                                FROM ordenes_venta
                                WHERE estado NOT IN ('Anulada', 'Rechazada')
                                """),
                        "MONEDA",
                        "Monto total de órdenes válidas registradas"
                ),
                new KpiResponse(
                        "VENTAS_MES",
                        "Ventas del mes",
                        bigDecimal("""
                                SELECT COALESCE(SUM(total), 0)
                                FROM ordenes_venta
                                WHERE estado NOT IN ('Anulada', 'Rechazada')
                                  AND fecha_orden >= ?
                                """, Timestamp.valueOf(inicioMes)),
                        "MONEDA",
                        "Monto vendido desde el inicio del mes"
                ),
                new KpiResponse(
                        "COBRADO_MES",
                        "Cobrado en caja",
                        bigDecimal("""
                                SELECT COALESCE(SUM(monto_pagado), 0)
                                FROM pagos_venta
                                WHERE estado = 'Pagado'
                                  AND fecha_pago >= ?
                                """, Timestamp.valueOf(inicioMes)),
                        "MONEDA",
                        "Pagos cancelados durante el mes"
                ),
                new KpiResponse(
                        "ORDENES_CONFIRMADAS",
                        "Órdenes confirmadas",
                        bigDecimal("""
                                SELECT COUNT(*)
                                FROM ordenes_venta
                                WHERE estado = 'Confirmada'
                                """),
                        "NUMERO",
                        "Órdenes listas para cobro en caja"
                ),
                new KpiResponse(
                        "STOCK_BAJO",
                        "Productos con stock bajo",
                        bigDecimal("""
                                SELECT COUNT(*)
                                FROM productos
                                WHERE COALESCE(stock_actual, 0) <= COALESCE(stock_minimo, 0)
                                """),
                        "NUMERO",
                        "Productos en nivel mínimo o crítico"
                ),
                new KpiResponse(
                        "VENCIMIENTO_30_DIAS",
                        "Vencen en 30 días",
                        bigDecimal("""
                                SELECT COUNT(*)
                                FROM productos
                                WHERE fecha_vencimiento BETWEEN CURRENT_DATE() AND DATE_ADD(CURRENT_DATE(), INTERVAL 30 DAY)
                                """),
                        "NUMERO",
                        "Productos próximos a vencer"
                ),
                new KpiResponse(
                        "FACTURAS_PROVEEDOR_PENDIENTES",
                        "Facturas proveedor pendientes",
                        bigDecimal("""
                                SELECT COUNT(*)
                                FROM facturas_compra
                                WHERE estado NOT IN ('Pagada', 'Cancelada')
                                """),
                        "NUMERO",
                        "Compras registradas pendientes de cierre"
                ),
                new KpiResponse(
                        "DESPACHOS_PENDIENTES",
                        "Despachos pendientes",
                        bigDecimal("""
                                SELECT COUNT(*)
                                FROM despachos
                                WHERE estado IN ('Pendiente', 'Preparado', 'En Ruta')
                                """),
                        "NUMERO",
                        "Entregas en tienda o domicilio aún no finalizadas"
                ),
                new KpiResponse(
                        "RECETAS_PENDIENTES",
                        "Recetas magistrales pendientes",
                        bigDecimal("""
                                SELECT COUNT(*)
                                FROM recetas_magistrales
                                WHERE estado NOT IN ('Entregada', 'Cancelada')
                                """),
                        "NUMERO",
                        "Recetas por preparar, pagar o entregar"
                ),
                new KpiResponse(
                        "ACCIONES_PENDIENTES",
                        "Acciones gerenciales pendientes",
                        bigDecimal("""
                                SELECT COUNT(*)
                                FROM acciones_gerencia
                                WHERE estado IN ('Pendiente', 'En Proceso')
                                """),
                        "NUMERO",
                        "Acciones de mejora registradas por gerencia"
                )
        );

        return new ReporteResumenResponse(
                kpis,
                obtenerSerie("""
                        SELECT estado AS etiqueta, COUNT(*) AS valor
                        FROM ordenes_venta
                        GROUP BY estado
                        ORDER BY valor DESC
                        """),
                obtenerSerie("""
                        SELECT canal_pedido AS etiqueta, COUNT(*) AS valor
                        FROM ordenes_venta
                        GROUP BY canal_pedido
                        ORDER BY valor DESC
                        """),
                obtenerSerie("""
                        SELECT metodo_pago AS etiqueta, COALESCE(SUM(monto_pagado), 0) AS valor
                        FROM pagos_venta
                        WHERE estado = 'Pagado'
                        GROUP BY metodo_pago
                        ORDER BY valor DESC
                        """),
                obtenerSerie("""
                        SELECT DATE(fecha_orden) AS etiqueta, COALESCE(SUM(total), 0) AS valor
                        FROM ordenes_venta
                        WHERE fecha_orden >= DATE_SUB(CURRENT_DATE(), INTERVAL 6 DAY)
                          AND estado NOT IN ('Anulada', 'Rechazada')
                        GROUP BY DATE(fecha_orden)
                        ORDER BY DATE(fecha_orden)
                        """),
                obtenerSerie("""
                        SELECT estado AS etiqueta, COUNT(*) AS valor
                        FROM acciones_gerencia
                        GROUP BY estado
                        ORDER BY valor DESC
                        """),
                obtenerStockCritico()
        );
    }

    public List<InformeResponse> listarInformes() {
        return jdbcTemplate.query("""
                SELECT i.id_informe,
                       i.area,
                       i.titulo,
                       i.descripcion,
                       i.fecha_emision,
                       CONCAT(t.nombres, ' ', t.apellidos) AS trabajador
                FROM informes i
                JOIN trabajadores t ON t.id_trabajador = i.id_trabajador
                ORDER BY i.fecha_emision DESC, i.id_informe DESC
                """, (rs, rowNum) -> new InformeResponse(
                rs.getInt("id_informe"),
                rs.getString("area"),
                rs.getString("titulo"),
                rs.getString("descripcion"),
                rs.getString("trabajador"),
                toLocalDateTime(rs.getTimestamp("fecha_emision"))
        ));
    }

    @Transactional
    public InformeResponse registrarInforme(RegistrarInformeRequest request) {
        Trabajador trabajador = authenticatedUserService.currentAccount().getTrabajador();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO informes (id_trabajador, area, titulo, descripcion, fecha_emision)
                    VALUES (?, ?, ?, ?, NOW())
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, trabajador.getIdTrabajador());
            ps.setString(2, limpiar(request.area()));
            ps.setString(3, limpiar(request.titulo()));
            ps.setString(4, limpiar(request.descripcion()));
            return ps;
        }, keyHolder);

        Integer idInforme = keyHolder.getKey() == null ? null : keyHolder.getKey().intValue();
        return obtenerInformePorId(idInforme);
    }

    public List<AccionGerenciaResponse> listarAcciones() {
        return jdbcTemplate.query("""
                SELECT a.id_accion,
                       a.id_informe,
                       i.titulo AS informe,
                       i.area,
                       CONCAT(t.nombres, ' ', t.apellidos) AS gerente,
                       a.accion_tomar,
                       a.fecha_registro,
                       a.estado
                FROM acciones_gerencia a
                JOIN informes i ON i.id_informe = a.id_informe
                JOIN trabajadores t ON t.id_trabajador = a.id_gerente
                ORDER BY a.fecha_registro DESC, a.id_accion DESC
                """, (rs, rowNum) -> new AccionGerenciaResponse(
                rs.getInt("id_accion"),
                rs.getInt("id_informe"),
                rs.getString("informe"),
                rs.getString("area"),
                rs.getString("gerente"),
                rs.getString("accion_tomar"),
                toLocalDateTime(rs.getTimestamp("fecha_registro")),
                rs.getString("estado")
        ));
    }

    @Transactional
    public AccionGerenciaResponse registrarAccion(RegistrarAccionRequest request) {
        validarExisteInforme(request.idInforme());
        Trabajador gerente = authenticatedUserService.currentAccount().getTrabajador();

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                    INSERT INTO acciones_gerencia (id_informe, id_gerente, accion_tomar, fecha_registro, estado)
                    VALUES (?, ?, ?, NOW(), 'Pendiente')
                    """, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, request.idInforme());
            ps.setInt(2, gerente.getIdTrabajador());
            ps.setString(3, limpiar(request.accionTomar()));
            return ps;
        }, keyHolder);

        Integer idAccion = keyHolder.getKey() == null ? null : keyHolder.getKey().intValue();
        return obtenerAccionPorId(idAccion);
    }

    @Transactional
    public AccionGerenciaResponse actualizarEstadoAccion(Integer idAccion, ActualizarEstadoAccionRequest request) {
        String estado = limpiar(request.estado());
        if (!List.of("Pendiente", "En Proceso", "Completada", "Cancelada").contains(estado)) {
            throw new IllegalArgumentException("Estado de acción no permitido");
        }

        int actualizados = jdbcTemplate.update(
                "UPDATE acciones_gerencia SET estado = ? WHERE id_accion = ?",
                estado,
                idAccion
        );

        if (actualizados == 0) {
            throw new IllegalArgumentException("La acción gerencial no existe");
        }

        return obtenerAccionPorId(idAccion);
    }

    private InformeResponse obtenerInformePorId(Integer idInforme) {
        if (idInforme == null) {
            throw new IllegalStateException("No se pudo obtener el identificador del informe registrado");
        }

        return jdbcTemplate.queryForObject("""
                SELECT i.id_informe,
                       i.area,
                       i.titulo,
                       i.descripcion,
                       i.fecha_emision,
                       CONCAT(t.nombres, ' ', t.apellidos) AS trabajador
                FROM informes i
                JOIN trabajadores t ON t.id_trabajador = i.id_trabajador
                WHERE i.id_informe = ?
                """, (rs, rowNum) -> new InformeResponse(
                rs.getInt("id_informe"),
                rs.getString("area"),
                rs.getString("titulo"),
                rs.getString("descripcion"),
                rs.getString("trabajador"),
                toLocalDateTime(rs.getTimestamp("fecha_emision"))
        ), idInforme);
    }

    private AccionGerenciaResponse obtenerAccionPorId(Integer idAccion) {
        if (idAccion == null) {
            throw new IllegalStateException("No se pudo obtener el identificador de la acción registrada");
        }

        return jdbcTemplate.queryForObject("""
                SELECT a.id_accion,
                       a.id_informe,
                       i.titulo AS informe,
                       i.area,
                       CONCAT(t.nombres, ' ', t.apellidos) AS gerente,
                       a.accion_tomar,
                       a.fecha_registro,
                       a.estado
                FROM acciones_gerencia a
                JOIN informes i ON i.id_informe = a.id_informe
                JOIN trabajadores t ON t.id_trabajador = a.id_gerente
                WHERE a.id_accion = ?
                """, (rs, rowNum) -> new AccionGerenciaResponse(
                rs.getInt("id_accion"),
                rs.getInt("id_informe"),
                rs.getString("informe"),
                rs.getString("area"),
                rs.getString("gerente"),
                rs.getString("accion_tomar"),
                toLocalDateTime(rs.getTimestamp("fecha_registro")),
                rs.getString("estado")
        ), idAccion);
    }

    private void validarExisteInforme(Integer idInforme) {
        Integer cantidad = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM informes WHERE id_informe = ?",
                Integer.class,
                idInforme
        );

        if (cantidad == null || cantidad == 0) {
            throw new IllegalArgumentException("El informe seleccionado no existe");
        }
    }

    private List<SerieItemResponse> obtenerSerie(String sql) {
        return jdbcTemplate.query(sql, (rs, rowNum) -> new SerieItemResponse(
                stringValue(rs.getObject("etiqueta")),
                toBigDecimal(rs.getObject("valor"))
        ));
    }

    private List<StockCriticoResponse> obtenerStockCritico() {
        return jdbcTemplate.query("""
                SELECT id_producto,
                       nombre,
                       stock_actual,
                       stock_minimo,
                       fecha_vencimiento
                FROM productos
                WHERE COALESCE(stock_actual, 0) <= COALESCE(stock_minimo, 0)
                   OR fecha_vencimiento BETWEEN CURRENT_DATE() AND DATE_ADD(CURRENT_DATE(), INTERVAL 30 DAY)
                ORDER BY COALESCE(stock_actual, 0) ASC, fecha_vencimiento ASC
                LIMIT 10
                """, (rs, rowNum) -> {
            Date fecha = rs.getDate("fecha_vencimiento");
            return new StockCriticoResponse(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getInt("stock_actual"),
                    rs.getInt("stock_minimo"),
                    fecha == null ? null : fecha.toLocalDate()
            );
        });
    }

    private BigDecimal bigDecimal(String sql, Object... params) {
        BigDecimal valor = jdbcTemplate.queryForObject(sql, BigDecimal.class, params);
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private BigDecimal toBigDecimal(Object valor) {
        if (valor == null) {
            return BigDecimal.ZERO;
        }
        if (valor instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (valor instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(valor.toString());
    }

    private String stringValue(Object valor) {
        if (valor == null) {
            return "Sin dato";
        }
        if (valor instanceof Date date) {
            return date.toLocalDate().toString();
        }
        if (valor instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate().toString();
        }
        return valor.toString();
    }

    private String limpiar(String valor) {
        if (valor == null) {
            return null;
        }
        return valor.trim();
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null
                ? LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
                : timestamp.toLocalDateTime();
    }
}
