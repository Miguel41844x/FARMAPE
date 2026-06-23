package com.farmape.backend.roles.service;

import com.farmape.backend.roles.dto.EstadoRolRequest;
import com.farmape.backend.roles.dto.RolRequest;
import com.farmape.backend.roles.dto.RolResponse;
import com.farmape.backend.roles.model.Permiso;
import com.farmape.backend.roles.model.Rol;
import com.farmape.backend.roles.repository.PermisoRepository;
import com.farmape.backend.roles.repository.RolRepository;
import com.farmape.backend.trabajadores.repository.TrabajadorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PermisoRepository permisoRepository;

    @Mock
    private TrabajadorRepository trabajadorRepository;

    @InjectMocks
    private RolService rolService;

    @Test
    void crearNormalizaCodigoYAsignaPermisosActivos() {
        Permiso permiso = Permiso.builder()
                .idPermiso(3)
                .codigo("PRODUCTOS_VER")
                .nombre("Ver productos")
                .modulo("Productos")
                .activo(true)
                .build();
        RolRequest request = new RolRequest(
                " jefe farmacia ",
                " Jefe Farmacia ",
                "Gestiona el area de farmacia",
                Set.of(3)
        );

        when(rolRepository.findByCodigo("JEFE_FARMACIA")).thenReturn(Optional.empty());
        when(rolRepository.findByNombreRol("Jefe Farmacia")).thenReturn(Optional.empty());
        when(permisoRepository.findAllById(Set.of(3))).thenReturn(List.of(permiso));
        when(rolRepository.save(any(Rol.class))).thenAnswer(invocation -> {
            Rol rol = invocation.getArgument(0);
            rol.setIdRol(6);
            return rol;
        });

        RolResponse response = rolService.crear(request);

        assertThat(response.idRol()).isEqualTo(6);
        assertThat(response.codigo()).isEqualTo("JEFE_FARMACIA");
        assertThat(response.nombreRol()).isEqualTo("Jefe Farmacia");
        assertThat(response.activo()).isTrue();
        assertThat(response.idPermisos()).containsExactly(3);

        ArgumentCaptor<Rol> captor = ArgumentCaptor.forClass(Rol.class);
        verify(rolRepository).save(captor.capture());
        assertThat(captor.getValue().getPermisos()).containsExactly(permiso);
    }

    @Test
    void crearLanzaErrorCuandoCodigoYaExiste() {
        RolRequest request = new RolRequest("admin", "Administrador", null, null);

        when(rolRepository.findByCodigo("ADMIN")).thenReturn(Optional.of(Rol.builder().idRol(1).build()));

        assertThatThrownBy(() -> rolService.crear(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rol");

        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    void cambiarEstadoNoPermiteDesactivarRolBase() {
        Rol rol = Rol.builder()
                .idRol(1)
                .codigo("ADMIN")
                .nombreRol("Administrador")
                .activo(true)
                .build();

        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));

        assertThatThrownBy(() -> rolService.cambiarEstado(1, new EstadoRolRequest(false)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("rol base");

        verify(rolRepository, never()).save(any(Rol.class));
    }
}
