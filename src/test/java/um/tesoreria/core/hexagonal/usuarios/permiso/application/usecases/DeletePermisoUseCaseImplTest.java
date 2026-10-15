package um.tesoreria.core.hexagonal.usuarios.permiso.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePermisoUseCaseImplTest {

    @Mock
    private PermisoRepository repository;

    @InjectMocks
    private DeletePermisoUseCaseImpl useCase;

    @Test
    void deletePermiso_whenExists_deletes() {
        when(repository.existsByPermisoId(1L)).thenReturn(true);

        useCase.deletePermiso(1L);

        verify(repository).deleteByPermisoId(1L);
    }

    @Test
    void deletePermiso_whenMissing_throws() {
        when(repository.existsByPermisoId(9L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.deletePermiso(9L)).isInstanceOf(PermisoException.class);
        verify(repository, never()).deleteByPermisoId(any());
    }
}
