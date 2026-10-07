package um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.GetPermisoByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.GetRolByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.exception.RolPermisoException;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.out.RolPermisoRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRolPermisoUseCaseImplTest {

    @Mock
    private RolPermisoRepository repository;
    @Mock
    private GetRolByIdUseCase getRolByIdUseCase;
    @Mock
    private GetPermisoByIdUseCase getPermisoByIdUseCase;

    @InjectMocks
    private CreateRolPermisoUseCaseImpl useCase;

    @Test
    void createRolPermiso_whenBothExist_saves() {
        when(getRolByIdUseCase.getRolById(5L)).thenReturn(Optional.of(Rol.builder().rolId(5L).build()));
        when(getPermisoByIdUseCase.getPermisoById(10L)).thenReturn(Optional.of(Permiso.builder().permisoId(10L).build()));
        when(repository.findByRolIdAndPermisoId(5L, 10L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.createRolPermiso(RolPermiso.builder().rolId(5L).permisoId(10L).build());

        assertThat(result.getRolId()).isEqualTo(5L);
        assertThat(result.getPermisoId()).isEqualTo(10L);
        verify(repository).save(any());
    }

    @Test
    void createRolPermiso_whenPermisoMissing_throws() {
        when(getRolByIdUseCase.getRolById(5L)).thenReturn(Optional.of(Rol.builder().rolId(5L).build()));
        when(getPermisoByIdUseCase.getPermisoById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.createRolPermiso(RolPermiso.builder().rolId(5L).permisoId(10L).build()))
                .isInstanceOf(RolPermisoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void createRolPermiso_whenRolIdNull_throws() {
        assertThatThrownBy(() -> useCase.createRolPermiso(RolPermiso.builder().permisoId(10L).build()))
                .isInstanceOf(RolPermisoException.class);
        verifyNoInteractions(repository);
    }
}
