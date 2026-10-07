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
class CreatePermisoUseCaseImplTest {

    @Mock
    private PermisoRepository repository;

    @InjectMocks
    private CreatePermisoUseCaseImpl useCase;

    @Test
    void createPermiso_savesWhenValid() {
        var permiso = Permiso.builder().clave("pagos.reembolsos").descripcion("Reembolsos").modulo("pagos").build();
        when(repository.findByAplicacionAndClave("TESORERIA", "pagos.reembolsos")).thenReturn(Optional.empty());
        when(repository.save(permiso)).thenReturn(permiso);

        assertThat(useCase.createPermiso(permiso)).isEqualTo(permiso);
        verify(repository).save(permiso);
    }

    @Test
    void createPermiso_whenDuplicateKey_throws() {
        var permiso = Permiso.builder().clave("pagos.reembolsos").descripcion("Reembolsos").modulo("pagos").build();
        when(repository.findByAplicacionAndClave("TESORERIA", "pagos.reembolsos"))
                .thenReturn(Optional.of(Permiso.builder().permisoId(1L).build()));

        assertThatThrownBy(() -> useCase.createPermiso(permiso)).isInstanceOf(PermisoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void createPermiso_whenBlankClave_throws() {
        assertThatThrownBy(() -> useCase.createPermiso(
                Permiso.builder().clave("  ").descripcion("d").modulo("m").build()))
                .isInstanceOf(PermisoException.class);
        verifyNoInteractions(repository);
    }
}
