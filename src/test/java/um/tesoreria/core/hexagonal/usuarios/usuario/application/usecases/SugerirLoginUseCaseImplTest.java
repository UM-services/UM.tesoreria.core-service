package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.exception.LoginNoDisponibleException;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SugerirLoginUseCaseImplTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private SugerirLoginUseCaseImpl useCase;

    @Test
    void devuelveElPrimerLoginLibre() {
        when(repository.existsByLoginIgnoreCase("jperez")).thenReturn(true);
        when(repository.existsByLoginIgnoreCase("juperez")).thenReturn(false);

        assertThat(useCase.sugerirLogin("Juan", "Pérez")).isEqualTo("juperez");
    }

    @Test
    void fallaSiTodosLosCandidatosEstanTomados() {
        when(repository.existsByLoginIgnoreCase(anyString())).thenReturn(true);

        assertThatThrownBy(() -> useCase.sugerirLogin("Juan", "Pérez"))
                .isInstanceOf(LoginNoDisponibleException.class);
    }
}
