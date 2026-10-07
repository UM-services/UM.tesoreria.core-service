package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.mapper.PermisoMapper;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.repository.JpaPermisoRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaPermisoRepositoryAdapterTest {

    @Mock
    private JpaPermisoRepository repository;

    @Mock
    private PermisoMapper mapper;

    @InjectMocks
    private JpaPermisoRepositoryAdapter adapter;

    @Test
    void deleteByPermisoId_whenHasAssignments_translatesFkViolationToPermisoException() {
        doThrow(new DataIntegrityViolationException("fk_rol_permiso_permiso"))
                .when(repository).deleteByPermisoId(1L);

        assertThatThrownBy(() -> adapter.deleteByPermisoId(1L))
                .isInstanceOf(PermisoException.class)
                .hasMessageContaining("asignados");
    }

    @Test
    void deleteByPermisoId_whenClean_deletesAndFlushes() {
        adapter.deleteByPermisoId(1L);

        verify(repository).deleteByPermisoId(1L);
        verify(repository).flush();
    }
}
