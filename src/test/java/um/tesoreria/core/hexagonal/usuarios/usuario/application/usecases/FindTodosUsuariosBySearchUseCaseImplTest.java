package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindTodosUsuariosBySearchUseCaseImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private FindTodosUsuariosBySearchUseCaseImpl useCase;

    @Test
    void blankTextBecomesNull() {
        when(usuarioRepository.findAllBySearch(null)).thenReturn(List.of(Usuario.builder().userId(1L).build()));

        assertThat(useCase.findTodosUsuariosBySearch("   ")).hasSize(1);
        verify(usuarioRepository).findAllBySearch(null);
    }

    @Test
    void textIsTrimmed() {
        when(usuarioRepository.findAllBySearch("pedro")).thenReturn(List.of());

        assertThat(useCase.findTodosUsuariosBySearch("  pedro ")).isEmpty();
        verify(usuarioRepository).findAllBySearch("pedro");
    }
}
