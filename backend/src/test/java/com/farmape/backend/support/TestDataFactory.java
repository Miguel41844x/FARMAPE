package com.farmape.backend.support;

import com.farmape.backend.roles.model.Permiso;
import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.trabajadores.enums.EstadoTrabajador;
import com.farmape.backend.trabajadores.model.Trabajador;
import com.farmape.backend.usuarios.enums.EstadoCuentaUsuario;
import com.farmape.backend.usuarios.model.CuentaUsuario;

import java.util.LinkedHashSet;
import java.util.List;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static CuentaUsuario cuentaAdministradorActiva() {
        Permiso permisoActivo = Permiso.builder()
                .idPermiso(1)
                .codigo("VENTAS_VER")
                .nombre("Ver ventas")
                .modulo("Ventas")
                .activo(true)
                .build();

        Permiso permisoInactivo = Permiso.builder()
                .idPermiso(2)
                .codigo("USUARIOS_EDITAR")
                .nombre("Editar usuarios")
                .modulo("Usuarios")
                .activo(false)
                .build();

        Rol rol = Rol.builder()
                .idRol(1)
                .codigo("ADMIN")
                .nombreRol("Administrador")
                .activo(true)
                .permisos(new LinkedHashSet<>(List.of(permisoActivo, permisoInactivo)))
                .build();

        Trabajador trabajador = Trabajador.builder()
                .idTrabajador(7)
                .dni("70000001")
                .nombres("Ana")
                .apellidos("Torres")
                .rol(rol)
                .estado(EstadoTrabajador.Activo)
                .build();

        return CuentaUsuario.builder()
                .idCuenta(10)
                .usuario("admin")
                .email("admin@farmape.pe")
                .clave("clave-codificada")
                .trabajador(trabajador)
                .estado(EstadoCuentaUsuario.Activo)
                .build();
    }
}
