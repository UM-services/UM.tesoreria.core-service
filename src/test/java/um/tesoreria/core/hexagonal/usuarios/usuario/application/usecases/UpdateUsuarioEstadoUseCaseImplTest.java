package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

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
class UpdateUsuarioEstadoUseCaseImplTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UpdateUsuarioEstadoUseCaseImpl useCase;

    @Test
    void updateEstado_setsActivo() {
        Usuario actual = Usuario.builder().userId(7L).login("pedro").password("h").activo((byte) 1).build();
        when(repository.findByUserId(7L)).thenReturn(Optional.of(actual));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario result = useCase.updateEstado(7L, (byte) 0).orElseThrow();

        assertThat(result.getActivo()).isEqualTo((byte) 0);
        assertThat(result.getPassword()).isEqualTo("h");
    }

    @Test
    void updateEstado_whenUserMissing_returnsEmpty() {
        when(repository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThat(useCase.updateEstado(99L, (byte) 1)).isEmpty();
    }
}
