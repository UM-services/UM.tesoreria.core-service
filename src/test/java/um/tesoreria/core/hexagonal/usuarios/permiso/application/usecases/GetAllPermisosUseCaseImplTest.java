package um.tesoreria.core.hexagonal.usuarios.permiso.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllPermisosUseCaseImplTest {

    @Mock
    private PermisoRepository repository;

    @InjectMocks
    private GetAllPermisosUseCaseImpl useCase;

    @Test
    void getAllPermisos_delegatesToRepository() {
        var results = List.of(Permiso.builder().permisoId(1L).build());
        when(repository.findAll()).thenReturn(results);

        assertThat(useCase.getAllPermisos()).isEqualTo(results);
        verify(repository).findAll();
    }
}
