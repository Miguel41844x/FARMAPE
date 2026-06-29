package com.farmape.backend.roles.service;

import com.farmape.backend.roles.dto.RolRequest;
import com.farmape.backend.roles.model.Permiso;
import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.roles.repository.PermisoRepository;
import com.farmape.backend.roles.repository.RolRepository;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {
    @Mock RolRepository rolRepository;
    @Mock PermisoRepository permisoRepository;
    @Mock TrabajadorRepository trabajadorRepository;
    @InjectMocks RolService rolService;

    @Test
    void crearNormalizaCodigoYAsignaPermisoActivo() {
        Permiso permiso = Permiso.builder().idPermiso(3).codigo("VENTAS_VER")
                .nombre("Ver ventas").modulo("Ventas").activo(true).build();
        RolRequest request = new RolRequest("jefe farmacia", "Jefe Farmacia", null, Set.of(3));
        when(rolRepository.findByCodigo("JEFE_FARMACIA")).thenReturn(Optional.empty());
        when(rolRepository.findByNombreRol("Jefe Farmacia")).thenReturn(Optional.empty());
        when(permisoRepository.findAllById(Set.of(3))).thenReturn(List.of(permiso));
        when(rolRepository.save(any())).thenAnswer(invocation -> {
            Rol rol = invocation.getArgument(0);
            rol.setIdRol(8);
            return rol;
        });

        var response = rolService.crear(request);

        assertThat(response.codigo()).isEqualTo("JEFE_FARMACIA");
        assertThat(response.idPermisos()).containsExactly(3);
    }

    @Test
    void crearRechazaCodigoDuplicado() {
        when(rolRepository.findByCodigo("ADMIN")).thenReturn(Optional.of(Rol.builder().idRol(1).build()));

        assertThatThrownBy(() -> rolService.crear(new RolRequest("admin", "Otro", null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("código");
    }
}
