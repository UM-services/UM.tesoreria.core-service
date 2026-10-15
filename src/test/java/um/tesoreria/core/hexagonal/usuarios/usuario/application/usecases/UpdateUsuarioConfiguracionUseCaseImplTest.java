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
class UpdateUsuarioConfiguracionUseCaseImplTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UpdateUsuarioConfiguracionUseCaseImpl useCase;

    @Test
    void updateConfiguracion_appliesFieldsAndPreservesLoginAndPassword() {
        Usuario actual = Usuario.builder()
                .userId(7L).login("pedro").password("hash-original").nombre("Pedro")
                .geograficaId(1).activo((byte) 1).build();
        when(repository.findByUserId(7L)).thenReturn(Optional.of(actual));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario cambios = Usuario.builder()
                .nombre("Pedro Pérez").geograficaId(3).googleMail("pedro@um.edu.ar")
                .imprimeChequera((byte) 1).numeroOpManual((byte) 0).habilitaOpEliminacion((byte) 1)
                .eliminaChequera((byte) 0).modificaChequera((byte) 1)
                .activo((byte) 1).administrador((byte) 1).usuarioExterno((byte) 0).build();

        Usuario result = useCase.updateConfiguracion(cambios, 7L).orElseThrow();

        assertThat(result.getNombre()).isEqualTo("Pedro Pérez");
        assertThat(result.getGeograficaId()).isEqualTo(3);
        assertThat(result.getAdministrador()).isEqualTo((byte) 1);
        assertThat(result.getImprimeChequera()).isEqualTo((byte) 1);
        // login y clave nunca se tocan
        assertThat(result.getLogin()).isEqualTo("pedro");
        assertThat(result.getPassword()).isEqualTo("hash-original");
    }

    @Test
    void updateConfiguracion_whenUserMissing_returnsEmpty() {
        when(repository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThat(useCase.updateConfiguracion(Usuario.builder().nombre("X").build(), 99L)).isEmpty();
    }
}
