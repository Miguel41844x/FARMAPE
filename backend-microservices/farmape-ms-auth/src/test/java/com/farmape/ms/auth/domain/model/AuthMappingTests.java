package com.farmape.ms.auth.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthMappingTests {

    @Test
    void estadoCuentaUsuarioTieneValoresEsperados() {
        assertThat(EstadoCuentaUsuario.values()).contains(
                EstadoCuentaUsuario.Activo,
                EstadoCuentaUsuario.Inactivo,
                EstadoCuentaUsuario.Bloqueado
        );
    }

    @Test
    void estadoTrabajadorTieneValoresEsperados() {
        assertThat(EstadoTrabajador.values()).contains(
                EstadoTrabajador.Activo,
                EstadoTrabajador.Inactivo
        );
    }
}