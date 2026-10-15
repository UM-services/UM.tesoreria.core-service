package um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.exception.RolPermisoException;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.out.RolPermisoRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRolPermisoUseCaseImplTest {

    @Mock
    private RolPermisoRepository repository;

    @InjectMocks
    private DeleteRolPermisoUseCaseImpl useCase;

    @Test
    void delete_whenExists_deletes() {
        when(repository.findByRolIdAndPermisoId(5L, 10L))
                .thenReturn(Optional.of(RolPermiso.builder().rolId(5L).permisoId(10L).build()));

        useCase.deleteRolPermiso(5L, 10L);

        verify(repository).deleteByRolIdAndPermisoId(5L, 10L);
    }

    @Test
    void delete_whenMissing_throws() {
        when(repository.findByRolIdAndPermisoId(5L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.deleteRolPermiso(5L, 10L)).isInstanceOf(RolPermisoException.class);
        verify(repository, never()).deleteByRolIdAndPermisoId(any(), any());
    }
}
