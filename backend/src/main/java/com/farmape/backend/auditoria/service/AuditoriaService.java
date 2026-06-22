package com.farmape.backend.auditoria.service;

import com.farmape.backend.auditoria.dto.AuditoriaEventoResponse;
import com.farmape.backend.auditoria.dto.AuditoriaResumenResponse;
import com.farmape.backend.auditoria.dto.RegistrarAuditoriaRequest;
import com.farmape.backend.auditoria.model.AuditoriaEvento;
import com.farmape.backend.auditoria.repository.AuditoriaEventoRepository;
import com.farmape.backend.security.AuthenticatedUserService;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AuditoriaService {

    private static final int LIMITE_MAXIMO = 500;

    private final AuditoriaEventoRepository auditoriaEventoRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public AuditoriaService(AuditoriaEventoRepository auditoriaEventoRepository,
                            AuthenticatedUserService authenticatedUserService) {
        this.auditoriaEventoRepository = auditoriaEventoRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Transactional(readOnly = true)
    public List<AuditoriaEventoResponse> listarEventos(String modulo,
                                                        String accion,
                                                        String severidad,
                                                        String usuario,
                                                        String busqueda,
                                                        LocalDate desde,
                                                        LocalDate hasta,
                                                        Integer limite) {
        int limit = normalizarLimite(limite);
        LocalDateTime fechaDesde = desde == null ? null : desde.atStartOfDay();
        LocalDateTime fechaHasta = hasta == null ? null : hasta.plusDays(1).atStartOfDay();

        Stream<AuditoriaEvento> stream = auditoriaEventoRepository
                .findAll(Sort.by(Sort.Direction.DESC, "fechaEvento"))
                .stream();

        stream = filtrarIgual(stream, modulo, AuditoriaEvento::getModulo);
        stream = filtrarIgual(stream, accion, AuditoriaEvento::getAccion);
        stream = filtrarIgual(stream, severidad, AuditoriaEvento::getSeveridad);
        stream = filtrarContiene(stream, usuario, AuditoriaEvento::getUsuario);

        if (fechaDesde != null) {
            stream = stream.filter(evento -> !evento.getFechaEvento().isBefore(fechaDesde));
        }
        if (fechaHasta != null) {
            stream = stream.filter(evento -> evento.getFechaEvento().isBefore(fechaHasta));
        }
        if (busqueda != null && !busqueda.isBlank()) {
            String q = busqueda.trim().toLowerCase(Locale.ROOT);
            stream = stream.filter(evento -> contiene(evento.getDescripcion(), q)
                    || contiene(evento.getEntidad(), q)
                    || contiene(evento.getEntidadId(), q)
                    || contiene(evento.getUsuario(), q)
                    || contiene(evento.getValorAnterior(), q)
                    || contiene(evento.getValorNuevo(), q));
        }

        return stream
                .limit(limit)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AuditoriaResumenResponse obtenerResumen() {
        List<AuditoriaEvento> eventos = auditoriaEventoRepository
                .findAll(Sort.by(Sort.Direction.DESC, "fechaEvento"));

        LocalDate hoy = LocalDate.now();
        long eventosHoy = eventos.stream()
                .filter(evento -> evento.getFechaEvento() != null && evento.getFechaEvento().toLocalDate().isEqual(hoy))
                .count();

        List<AuditoriaResumenResponse.ModuloAuditoriaResumen> modulos = agrupar(eventos, AuditoriaEvento::getModulo)
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(8)
                .map(entry -> new AuditoriaResumenResponse.ModuloAuditoriaResumen(entry.getKey(), entry.getValue()))
                .toList();

        List<AuditoriaResumenResponse.AccionAuditoriaResumen> acciones = agrupar(eventos, AuditoriaEvento::getAccion)
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(8)
                .map(entry -> new AuditoriaResumenResponse.AccionAuditoriaResumen(entry.getKey(), entry.getValue()))
                .toList();

        List<AuditoriaEventoResponse> recientes = eventos.stream()
                .limit(10)
                .map(this::toResponse)
                .toList();

        return new AuditoriaResumenResponse(
                eventos.size(),
                eventosHoy,
                contarModulo(eventos, "Ventas"),
                contarModulo(eventos, "Compras"),
                contarModulo(eventos, "Almacén") + contarModulo(eventos, "Inventario"),
                contarModulo(eventos, "Usuarios") + contarModulo(eventos, "Roles") + contarModulo(eventos, "Seguridad"),
                eventos.stream().filter(this::esEventoRiesgo).count(),
                modulos,
                acciones,
                recientes
        );
    }

    @Transactional
    public AuditoriaEventoResponse registrarManual(RegistrarAuditoriaRequest request, String ip) {
        CuentaUsuario cuenta = authenticatedUserService.currentAccount();
        Trabajador trabajador = cuenta.getTrabajador();

        AuditoriaEvento evento = AuditoriaEvento.builder()
                .fechaEvento(LocalDateTime.now())
                .modulo(normalizarTexto(request.modulo(), "General"))
                .entidad(normalizarTexto(request.entidad(), "Evento"))
                .entidadId(limpiar(request.entidadId()))
                .accion(normalizarTexto(request.accion(), "REGISTRO_MANUAL").toUpperCase(Locale.ROOT))
                .descripcion(normalizarTexto(request.descripcion(), "Evento registrado manualmente"))
                .valorAnterior(limpiar(request.valorAnterior()))
                .valorNuevo(limpiar(request.valorNuevo()))
                .idUsuario(cuenta.getIdCuenta())
                .idTrabajador(trabajador != null ? trabajador.getIdTrabajador() : null)
                .usuario(cuenta.getUsuario())
                .severidad(normalizarSeveridad(request.severidad()))
                .origen("APP")
                .ip(ip)
                .build();

        return toResponse(auditoriaEventoRepository.save(evento));
    }

    private Stream<AuditoriaEvento> filtrarIgual(Stream<AuditoriaEvento> stream,
                                                 String filtro,
                                                 Function<AuditoriaEvento, String> extractor) {
        if (filtro == null || filtro.isBlank()) {
            return stream;
        }
        String valor = filtro.trim();
        return stream.filter(evento -> valor.equalsIgnoreCase(extractor.apply(evento)));
    }

    private Stream<AuditoriaEvento> filtrarContiene(Stream<AuditoriaEvento> stream,
                                                    String filtro,
                                                    Function<AuditoriaEvento, String> extractor) {
        if (filtro == null || filtro.isBlank()) {
            return stream;
        }
        String valor = filtro.trim().toLowerCase(Locale.ROOT);
        return stream.filter(evento -> contiene(extractor.apply(evento), valor));
    }

    private boolean contiene(String texto, String busqueda) {
        return texto != null && texto.toLowerCase(Locale.ROOT).contains(busqueda);
    }

    private Map<String, Long> agrupar(List<AuditoriaEvento> eventos, Function<AuditoriaEvento, String> extractor) {
        return eventos.stream()
                .map(extractor)
                .filter(valor -> valor != null && !valor.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private long contarModulo(List<AuditoriaEvento> eventos, String modulo) {
        return eventos.stream()
                .filter(evento -> modulo.equalsIgnoreCase(evento.getModulo()))
                .count();
    }

    private boolean esEventoRiesgo(AuditoriaEvento evento) {
        return "ALTA".equalsIgnoreCase(evento.getSeveridad()) || "CRITICA".equalsIgnoreCase(evento.getSeveridad());
    }

    private int normalizarLimite(Integer limite) {
        if (limite == null || limite < 1) {
            return 100;
        }
        return Math.min(limite, LIMITE_MAXIMO);
    }

    private String normalizarSeveridad(String severidad) {
        if (severidad == null || severidad.isBlank()) {
            return "INFO";
        }
        String valor = severidad.trim().toUpperCase(Locale.ROOT);
        return switch (valor) {
            case "INFO", "BAJA", "MEDIA", "ALTA", "CRITICA" -> valor;
            default -> "INFO";
        };
    }

    private String normalizarTexto(String texto, String defecto) {
        String limpio = limpiar(texto);
        return limpio == null ? defecto : limpio;
    }

    private String limpiar(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim();
        return limpio.isEmpty() ? null : limpio;
    }

    private AuditoriaEventoResponse toResponse(AuditoriaEvento evento) {
        return new AuditoriaEventoResponse(
                evento.getIdAuditoria(),
                evento.getFechaEvento(),
                evento.getModulo(),
                evento.getEntidad(),
                evento.getEntidadId(),
                evento.getAccion(),
                evento.getDescripcion(),
                evento.getValorAnterior(),
                evento.getValorNuevo(),
                evento.getIdUsuario(),
                evento.getIdTrabajador(),
                evento.getUsuario(),
                evento.getSeveridad(),
                evento.getOrigen(),
                evento.getIp()
        );
    }
}
