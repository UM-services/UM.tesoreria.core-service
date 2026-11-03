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
class UpdateUsuarioUseCaseImplTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UpdateUsuarioUseCaseImpl useCase;

    @Test
    void updateUsuario_sinGoogleMail_conservaElExistente() {
        Usuario actual = Usuario.builder()
                .userId(7L).login("pedro").password("hash-original").nombre("Pedro")
                .geograficaId(1).googleMail("pedro@um.edu.ar")
                .modificaChequera((byte) 0).activo((byte) 1).build();
        when(repository.findByUserId(7L)).thenReturn(Optional.of(actual));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario cambios = Usuario.builder()
                .login("pedro").password("nueva123").nombre("Pedro").geograficaId(1)
                .imprimeChequera((byte) 0).numeroOpManual((byte) 0).habilitaOpEliminacion((byte) 0)
                .eliminaChequera((byte) 0).modificaChequera((byte) 1).activo((byte) 1).build();

        Usuario result = useCase.updateUsuario(cambios, 7L);

        assertThat(result.getGoogleMail()).isEqualTo("pedro@um.edu.ar");
        assertThat(result.getModificaChequera()).isEqualTo((byte) 1);
    }

    @Test
    void updateUsuario_conGoogleMail_loActualiza() {
        Usuario actual = Usuario.builder()
                .userId(7L).login("pedro").password("hash-original").nombre("Pedro")
                .geograficaId(1).googleMail("pedro@um.edu.ar").build();
        when(repository.findByUserId(7L)).thenReturn(Optional.of(actual));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario cambios = Usuario.builder()
                .login("pedro").password("nueva123").nombre("Pedro").geograficaId(1)
                .googleMail("nuevo@um.edu.ar").build();

        Usuario result = useCase.updateUsuario(cambios, 7L);

        assertThat(result.getGoogleMail()).isEqualTo("nuevo@um.edu.ar");
    }

    @Test
    void updateUsuario_usuarioInexistente_devuelveNull() {
        when(repository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThat(useCase.updateUsuario(Usuario.builder().login("x").password("x").build(), 99L)).isNull();
    }
}
