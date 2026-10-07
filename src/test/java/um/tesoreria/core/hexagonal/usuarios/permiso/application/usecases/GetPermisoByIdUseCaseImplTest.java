package um.tesoreria.core.hexagonal.usuarios.permiso.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPermisoByIdUseCaseImplTest {

    @Mock
    private PermisoRepository repository;

    @InjectMocks
    private GetPermisoByIdUseCaseImpl useCase;

    @Test
    void getPermisoById_delegatesToRepository() {
        var permiso = Permiso.builder().permisoId(1L).clave("chequeras.eliminar").build();
        when(repository.findByPermisoId(1L)).thenReturn(Optional.of(permiso));

        assertThat(useCase.getPermisoById(1L)).contains(permiso);
        verify(repository).findByPermisoId(1L);
    }

    @Test
    void getPermisoById_whenMissing_returnsEmpty() {
        when(repository.findByPermisoId(99L)).thenReturn(Optional.empty());

        assertThat(useCase.getPermisoById(99L)).isEmpty();
    }
}
