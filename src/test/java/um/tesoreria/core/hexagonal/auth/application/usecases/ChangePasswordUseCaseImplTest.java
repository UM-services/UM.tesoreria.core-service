package um.tesoreria.core.hexagonal.auth.application.usecases;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.domain.ports.out.UsuarioAuthRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordUseCaseImplTest {

    @Mock
    private UsuarioAuthRepository usuarioAuthRepository;

    @InjectMocks
    private ChangePasswordUseCaseImpl useCase;

    private UsuarioAuth usuario;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioAuth();
        usuario.setUserId(10L);
        usuario.setLogin("operador1");
        usuario.setPassword(DigestUtils.sha256Hex("claveVieja123"));
        usuario.setNombre("Operador Uno");
        usuario.setActivo((byte) 1);
    }

    @Test
    void changePassword_whenNewPasswordIsEmpty_throwsException() {
        assertThatThrownBy(() -> useCase.changePassword(10L, "operador1", "claveVieja123", "", "", "Operador Uno"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ERROR: Falta CLAVE . . .");
    }

    @Test
    void changePassword_whenPasswordsDoNotMatch_throwsException() {
        assertThatThrownBy(() -> useCase.changePassword(10L, "operador1", "claveVieja123", "nuevaClave1", "otraClave2", "Operador"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ERROR: Claves NO Coinciden");
    }

    @Test
    void changePassword_whenUserNotFound_throwsException() {
        when(usuarioAuthRepository.findById(99L)).thenReturn(Optional.empty());
        when(usuarioAuthRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.changePassword(99L, "inexistente", "claveVieja123", "nueva123", "nueva123", "Nombre"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ERROR: Usuario NO Encontrado");
    }

    @Test
    void changePassword_whenUserIsAdmin_throwsException() {
        UsuarioAuth admin = new UsuarioAuth();
        admin.setUserId(1L);
        admin.setLogin("adminGeneral");
        when(usuarioAuthRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> useCase.changePassword(1L, "adminGeneral", "claveVieja123", "nueva123", "nueva123", "Admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ERROR: NO se puede Cambiar ESTA Clave");
    }

    @Test
    void changePassword_whenCurrentPasswordIncorrect_throwsException() {
        when(usuarioAuthRepository.findById(10L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> useCase.changePassword(10L, "operador1", "claveIncorrecta", "nueva123", "nueva123", "Operador"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ERROR: Usuario NO Autenticado");
    }

    @Test
    void changePassword_whenNewPasswordAlreadyUsedByAnotherUser_throwsException() {
        when(usuarioAuthRepository.findById(10L)).thenReturn(Optional.of(usuario));

        UsuarioAuth otroUsuario = new UsuarioAuth();
        otroUsuario.setUserId(20L);
        otroUsuario.setLogin("otroUser");

        String newHash = DigestUtils.sha256Hex("claveComun");
        when(usuarioAuthRepository.findByPassword(newHash)).thenReturn(Optional.of(otroUsuario));

        assertThatThrownBy(() -> useCase.changePassword(10L, "operador1", "claveVieja123", "claveComun", "claveComun", "Operador"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ERROR: Clave NO Válida");
    }

    @Test
    void changePassword_successfulUpdate() {
        when(usuarioAuthRepository.findById(10L)).thenReturn(Optional.of(usuario));
        String newHash = DigestUtils.sha256Hex("nuevaClaveSegura123");
        when(usuarioAuthRepository.findByPassword(newHash)).thenReturn(Optional.empty());
        when(usuarioAuthRepository.save(any(UsuarioAuth.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioAuth result = useCase.changePassword(10L, "operador1", "claveVieja123", "nuevaClaveSegura123", "nuevaClaveSegura123", "Operador Renombrado");

        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isEqualTo(newHash);
        assertThat(result.getNombre()).isEqualTo("Operador Renombrado");
        verify(usuarioAuthRepository).save(usuario);
    }
}
