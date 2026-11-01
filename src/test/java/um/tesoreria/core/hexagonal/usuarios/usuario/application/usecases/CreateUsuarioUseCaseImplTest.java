package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.exception.LoginNoDisponibleException;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUsuarioUseCaseImplTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private CreateUsuarioUseCaseImpl useCase;

    private Usuario alta(String nombre, String apellido) {
        return Usuario.builder()
                .nombre(nombre)
                .apellido(apellido)
                .password("secreta")
                .geograficaId(1)
                .build();
    }

    @Test
    void generaLoginCuandoNoVieneYComponeElNombreCompleto() {
        when(repository.existsByLoginIgnoreCase(anyString())).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario creado = useCase.createUsuario(alta("Juan", "Pérez"));

        assertThat(creado.getLogin()).isEqualTo("jperez");
        assertThat(creado.getNombre()).isEqualTo("Juan Pérez");
        assertThat(creado.getPassword()).isEqualTo(DigestUtils.sha256Hex("secreta"));
    }

    @Test
    void saltaLoginsYaTomados() {
        when(repository.existsByLoginIgnoreCase("jperez")).thenReturn(true);
        when(repository.existsByLoginIgnoreCase("juperez")).thenReturn(true);
        when(repository.existsByLoginIgnoreCase("juaperez")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario creado = useCase.createUsuario(alta("Juan", "Pérez"));

        assertThat(creado.getLogin()).isEqualTo("juaperez");
    }

    @Test
    void reintentaAnteColisionEnElInsert() {
        when(repository.existsByLoginIgnoreCase(anyString())).thenReturn(false);
        when(repository.save(any()))
                .thenThrow(new DataIntegrityViolationException("duplicado"))
                .thenAnswer(inv -> inv.getArgument(0));

        Usuario creado = useCase.createUsuario(alta("Juan", "Pérez"));

        assertThat(creado.getLogin()).isEqualTo("juperez");
    }

    @Test
    void respetaUnLoginExplicitoParaClientesPrevios() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Usuario usuario = alta("Pedro", null);
        usuario.setLogin("pedro");

        Usuario creado = useCase.createUsuario(usuario);

        assertThat(creado.getLogin()).isEqualTo("pedro");
        verify(repository, never()).existsByLoginIgnoreCase(anyString());
    }

    @Test
    void loginExplicitoDuplicadoDevuelveErrorClaro() {
        when(repository.save(any())).thenThrow(new DataIntegrityViolationException("duplicado"));
        Usuario usuario = alta("Pedro", null);
        usuario.setLogin("pedro");

        assertThatThrownBy(() -> useCase.createUsuario(usuario))
                .isInstanceOf(LoginNoDisponibleException.class)
                .hasMessageContaining("pedro");
    }

    @Test
    void sinApellidoYsinLoginFallaConValidacion() {
        assertThatThrownBy(() -> useCase.createUsuario(alta("Pedro", " ")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void siNoHayNingunLoginDisponibleFalla() {
        when(repository.existsByLoginIgnoreCase(anyString())).thenReturn(true);

        assertThatThrownBy(() -> useCase.createUsuario(alta("Juan", "Pérez")))
                .isInstanceOf(LoginNoDisponibleException.class);
    }
}
