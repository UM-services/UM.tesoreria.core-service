package um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.GetRolByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.GetUsuarioByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.exception.UsuarioRolException;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.out.UsuarioRolRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUsuarioRolUseCaseImplTest {

    @Mock
    private UsuarioRolRepository repository;
    @Mock
    private GetUsuarioByIdUseCase getUsuarioByIdUseCase;
    @Mock
    private GetRolByIdUseCase getRolByIdUseCase;

    @InjectMocks
    private CreateUsuarioRolUseCaseImpl useCase;

    @Test
    void createUsuarioRol_whenBothExist_saves() {
        when(getUsuarioByIdUseCase.getUsuarioById(1L)).thenReturn(Optional.of(Usuario.builder().userId(1L).build()));
        when(getRolByIdUseCase.getRolById(5L)).thenReturn(Optional.of(Rol.builder().rolId(5L).build()));
        when(repository.findByUserIdAndRolId(1L, 5L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.createUsuarioRol(UsuarioRol.builder().userId(1L).rolId(5L).build());

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getRolId()).isEqualTo(5L);
        verify(repository).save(any());
    }

    @Test
    void createUsuarioRol_whenUsuarioMissing_throws() {
        when(getUsuarioByIdUseCase.getUsuarioById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.createUsuarioRol(UsuarioRol.builder().userId(1L).rolId(5L).build()))
                .isInstanceOf(UsuarioRolException.class);
        verify(repository, never()).save(any());
    }
}
