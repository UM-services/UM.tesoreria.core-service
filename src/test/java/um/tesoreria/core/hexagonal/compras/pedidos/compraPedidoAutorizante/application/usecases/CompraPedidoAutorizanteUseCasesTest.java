package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.application.exception.CompraPedidoAutorizanteException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.out.CompraPedidoAutorizanteRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraPedidoAutorizanteUseCasesTest {

    @Mock
    private CompraPedidoAutorizanteRepository repository;

    @Test
    void getDependenciasDelegaEnElRepositorio() {
        when(repository.findDependenciaIdsByAutorizanteId(9)).thenReturn(List.of(1, 2));
        GetDependenciasAutorizadasUseCaseImpl useCase = new GetDependenciasAutorizadasUseCaseImpl(repository);

        assertThat(useCase.getDependencias(9)).containsExactly(1, 2);
    }

    @Test
    void getDependenciasExigeAutorizante() {
        GetDependenciasAutorizadasUseCaseImpl useCase = new GetDependenciasAutorizadasUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.getDependencias(null))
                .isInstanceOf(CompraPedidoAutorizanteException.class);
    }

    @Test
    void asignarDelegaEnElRepositorio() {
        AsignarDependenciaAutorizanteUseCaseImpl useCase = new AsignarDependenciaAutorizanteUseCaseImpl(repository);

        useCase.asignar(9, 3);

        verify(repository).asignar(9, 3);
    }

    @Test
    void asignarExigeAutorizanteYDependencia() {
        AsignarDependenciaAutorizanteUseCaseImpl useCase = new AsignarDependenciaAutorizanteUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.asignar(9, null))
                .isInstanceOf(CompraPedidoAutorizanteException.class);
    }

    @Test
    void quitarDelegaEnElRepositorio() {
        QuitarDependenciaAutorizanteUseCaseImpl useCase = new QuitarDependenciaAutorizanteUseCaseImpl(repository);

        useCase.quitar(9, 3);

        verify(repository).quitar(9, 3);
    }

    @Test
    void quitarExigeAutorizanteYDependencia() {
        QuitarDependenciaAutorizanteUseCaseImpl useCase = new QuitarDependenciaAutorizanteUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.quitar(null, 3))
                .isInstanceOf(CompraPedidoAutorizanteException.class);
    }

}
