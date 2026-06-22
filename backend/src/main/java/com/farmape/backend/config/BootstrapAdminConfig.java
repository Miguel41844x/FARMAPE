package com.farmape.backend.config;

import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.roles.repository.RolRepository;
import com.farmape.backend.trabajadores.enums.EstadoTrabajador;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import com.farmape.backend.usuarios.enums.EstadoCuentaUsuario;
import com.farmape.backend.usuarios.model.CuentaUsuario;
import com.farmape.backend.usuarios.repository.CuentaUsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
public class BootstrapAdminConfig implements ApplicationRunner {

    private final CuentaUsuarioRepository cuentaRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled:false}")
    private boolean enabled;
    @Value("${app.bootstrap.admin.usuario:}")
    private String usuario;
    @Value("${app.bootstrap.admin.email:}")
    private String email;
    @Value("${app.bootstrap.admin.clave:}")
    private String clave;
    @Value("${app.bootstrap.admin.dni:}")
    private String dni;
    @Value("${app.bootstrap.admin.nombres:Administrador}")
    private String nombres;
    @Value("${app.bootstrap.admin.apellidos:FARMAPE}")
    private String apellidos;

    public BootstrapAdminConfig(
            CuentaUsuarioRepository cuentaRepository,
            TrabajadorRepository trabajadorRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.cuentaRepository = cuentaRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!enabled || cuentaRepository.count() > 0) {
            return;
        }

        if (!StringUtils.hasText(usuario) || !StringUtils.hasText(email)
                || !StringUtils.hasText(clave) || clave.length() < 12
                || !StringUtils.hasText(dni)) {
            throw new IllegalStateException(
                    "BOOTSTRAP_ADMIN_* es obligatorio y la contraseña debe tener al menos 12 caracteres"
            );
        }

        Rol administrador = rolRepository.findByCodigo("ADMIN")
                .filter(rol -> Boolean.TRUE.equals(rol.getActivo()))
                .orElseThrow(() -> new IllegalStateException("No existe el rol ADMIN activo"));

        Trabajador trabajador = trabajadorRepository.save(Trabajador.builder()
                .rol(administrador)
                .dni(dni.trim())
                .nombres(nombres.trim())
                .apellidos(apellidos.trim())
                .estado(EstadoTrabajador.Activo)
                .fechaRegistro(LocalDateTime.now())
                .build());

        cuentaRepository.save(CuentaUsuario.builder()
                .trabajador(trabajador)
                .usuario(usuario.trim())
                .email(email.trim().toLowerCase())
                .clave(passwordEncoder.encode(clave))
                .estado(EstadoCuentaUsuario.Activo)
                .fechaCreacion(LocalDateTime.now())
                .build());
    }
}
