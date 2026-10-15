package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResetUsuarioPasswordUseCaseImplTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private ResetUsuarioPasswordUseCaseImpl useCase;

    @Test
    void resetPassword_hashesNewPassword() {
        Usuario actual = Usuario.builder().userId(7L).login("pedro").password("viejo").build();
        when(repository.findByUserId(7L)).thenReturn(Optional.of(actual));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario result = useCase.resetPassword(7L, "nueva123").orElseThrow();

        assertThat(result.getPassword()).isEqualTo(DigestUtils.sha256Hex("nueva123"));
        assertThat(result.getLogin()).isEqualTo("pedro");
        // el reset de admin fuerza el cambio al proximo ingreso
        assertThat(result.getDebeCambiarClave()).isEqualTo((byte) 1);
    }

    @Test
    void resetPassword_whenUserMissing_returnsEmpty() {
        when(repository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThat(useCase.resetPassword(99L, "x")).isEmpty();
    }
}
