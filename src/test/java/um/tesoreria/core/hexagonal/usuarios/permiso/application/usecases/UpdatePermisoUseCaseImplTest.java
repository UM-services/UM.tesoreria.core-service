package um.tesoreria.core.hexagonal.usuarios.permiso.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePermisoUseCaseImplTest {

    @Mock
    private PermisoRepository repository;

    @InjectMocks
    private UpdatePermisoUseCaseImpl useCase;

    @Test
    void updatePermiso_whenExists_updates() {
        var existing = Permiso.builder().permisoId(1L).clave("pagos.reembolsos")
                .descripcion("vieja").modulo("pagos").build();
        var cambios = Permiso.builder().clave("pagos.reembolsos").descripcion("nueva")
                .modulo("pagos").activo((byte) 0).build();
        when(repository.findByPermisoId(1L)).thenReturn(Optional.of(existing));
        when(repository.findByAplicacionAndClave("TESORERIA", "pagos.reembolsos")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        var result = useCase.updatePermiso(cambios, 1L);

        assertThat(result.getDescripcion()).isEqualTo("nueva");
        assertThat(result.getActivo()).isEqualTo((byte) 0);
    }

    @Test
    void updatePermiso_whenKeyCollidesWithOther_throws() {
        var existing = Permiso.builder().permisoId(1L).clave("pagos.reembolsos").modulo("pagos").build();
        var otro = Permiso.builder().permisoId(2L).clave("pagos.reembolsos").build();
        when(repository.findByPermisoId(1L)).thenReturn(Optional.of(existing));
        when(repository.findByAplicacionAndClave("TESORERIA", "pagos.reembolsos")).thenReturn(Optional.of(otro));

        assertThatThrownBy(() -> useCase.updatePermiso(
                Permiso.builder().clave("pagos.reembolsos").build(), 1L))
                .isInstanceOf(PermisoException.class);
    }

    @Test
    void updatePermiso_whenMissing_returnsNull() {
        when(repository.findByPermisoId(9L)).thenReturn(Optional.empty());

        assertThat(useCase.updatePermiso(Permiso.builder().clave("x.y").build(), 9L)).isNull();
    }
}
