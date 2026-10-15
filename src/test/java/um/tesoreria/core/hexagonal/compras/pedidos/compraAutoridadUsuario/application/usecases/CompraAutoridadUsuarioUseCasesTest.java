package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.exception.CompraAutoridadUsuarioException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.out.CompraAutoridadUsuarioRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraAutoridadUsuarioUseCasesTest {

    @Mock
    private CompraAutoridadUsuarioRepository repository;

    @Test
    void asignarDelega() {
        new AsignarAutoridadUsuarioUseCaseImpl(repository).asignar(9, 5L);
        verify(repository).asignar(9, 5L);
    }

    @Test
    void quitarDelega() {
        new QuitarAutoridadUsuarioUseCaseImpl(repository).quitar(9, 5L);
        verify(repository).quitar(9, 5L);
    }

    @Test
    void getPerfilIdsDelega() {
        when(repository.findPerfilIdsByUsuarioId(9)).thenReturn(List.of(5L, 6L));

        assertThat(new GetPerfilIdsByUsuarioUseCaseImpl(repository).getPerfilIds(9)).containsExactly(5L, 6L);
    }

    @Test
    void getPerfilIdsExigeUsuario() {
        assertThatThrownBy(() -> new GetPerfilIdsByUsuarioUseCaseImpl(repository).getPerfilIds(null))
                .isInstanceOf(CompraAutoridadUsuarioException.class);
    }

}
