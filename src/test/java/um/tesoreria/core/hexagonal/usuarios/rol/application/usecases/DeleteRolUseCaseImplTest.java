package um.tesoreria.core.hexagonal.usuarios.rol.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolException;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.out.RolRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRolUseCaseImplTest {

    @Mock
    private RolRepository repository;

    @InjectMocks
    private DeleteRolUseCaseImpl useCase;

    @Test
    void deleteRol_whenExists_deletes() {
        when(repository.existsByRolId(1L)).thenReturn(true);

        useCase.deleteRol(1L);

        verify(repository).deleteByRolId(1L);
    }

    @Test
    void deleteRol_whenMissing_throws() {
        when(repository.existsByRolId(99L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.deleteRol(99L)).isInstanceOf(RolException.class);
        verify(repository, never()).deleteByRolId(any());
    }
}
