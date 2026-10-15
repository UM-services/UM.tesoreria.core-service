package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.GetPermisoByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.GetUsuarioByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.exception.UsuarioPermisoException;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.out.UsuarioPermisoRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetUsuarioPermisoUseCaseImplTest {

    @Mock
    private UsuarioPermisoRepository repository;
    @Mock
    private GetUsuarioByIdUseCase getUsuarioByIdUseCase;
    @Mock
    private GetPermisoByIdUseCase getPermisoByIdUseCase;

    @InjectMocks
    private SetUsuarioPermisoUseCaseImpl useCase;

    @Test
    void set_whenNoOverride_createsIt() {
        when(getUsuarioByIdUseCase.getUsuarioById(1L)).thenReturn(Optional.of(Usuario.builder().userId(1L).build()));
        when(getPermisoByIdUseCase.getPermisoById(10L)).thenReturn(Optional.of(Permiso.builder().permisoId(10L).build()));
        when(repository.findByUserIdAndPermisoId(1L, 10L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.setUsuarioPermiso(1L, 10L, (byte) 1);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getPermisoId()).isEqualTo(10L);
        assertThat(result.getOtorgado()).isEqualTo((byte) 1);
    }

    @Test
    void set_whenOverrideExists_updatesFlag() {
        var existing = UsuarioPermiso.builder().usuarioPermisoId(7L).userId(1L).permisoId(10L).otorgado((byte) 1).build();
        when(getUsuarioByIdUseCase.getUsuarioById(1L)).thenReturn(Optional.of(Usuario.builder().userId(1L).build()));
        when(getPermisoByIdUseCase.getPermisoById(10L)).thenReturn(Optional.of(Permiso.builder().permisoId(10L).build()));
        when(repository.findByUserIdAndPermisoId(1L, 10L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.setUsuarioPermiso(1L, 10L, (byte) 0);

        assertThat(result.getUsuarioPermisoId()).isEqualTo(7L);
        assertThat(result.getOtorgado()).isEqualTo((byte) 0);
    }

    @Test
    void set_whenOtorgadoInvalid_throws() {
        assertThatThrownBy(() -> useCase.setUsuarioPermiso(1L, 10L, (byte) 2))
                .isInstanceOf(UsuarioPermisoException.class);
        verifyNoInteractions(repository, getUsuarioByIdUseCase, getPermisoByIdUseCase);
    }
}
