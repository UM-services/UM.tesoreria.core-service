package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolException;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.mapper.RolMapper;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.repository.JpaRolRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaRolRepositoryAdapterTest {

    @Mock
    private JpaRolRepository repository;

    @Mock
    private RolMapper mapper;

    @InjectMocks
    private JpaRolRepositoryAdapter adapter;

    @Test
    void deleteByRolId_whenHasAssignments_translatesFkViolationToRolException() {
        // La FK real vive en el DDL de MySQL (usuario_rol / rol_permiso -> rol):
        // aquí se simula la violación para verificar la traducción a RolException.
        doThrow(new DataIntegrityViolationException("fk_rol_permiso_rol"))
                .when(repository).deleteByRolId(1L);

        assertThatThrownBy(() -> adapter.deleteByRolId(1L))
                .isInstanceOf(RolException.class)
                .hasMessageContaining("asignados");
    }

    @Test
    void deleteByRolId_whenClean_deletesAndFlushes() {
        adapter.deleteByRolId(1L);

        verify(repository).deleteByRolId(1L);
        verify(repository).flush();
    }
}
