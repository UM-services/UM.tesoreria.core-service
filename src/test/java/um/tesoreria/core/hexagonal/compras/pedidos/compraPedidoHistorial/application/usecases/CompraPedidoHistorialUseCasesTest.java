package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.ports.out.CompraPedidoHistorialRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompraPedidoHistorialUseCasesTest {

    @Mock
    private CompraPedidoHistorialRepository repository;

    @Test
    void registrarCompletaLaFechaYDelega() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        RegistrarCompraPedidoHistorialUseCaseImpl useCase = new RegistrarCompraPedidoHistorialUseCaseImpl(repository);

        CompraPedidoHistorial historial = useCase.registrar(5, "ENVIADO", 9, "ok");

        assertThat(historial.compraPedidoId()).isEqualTo(5);
        assertThat(historial.estado()).isEqualTo("ENVIADO");
        assertThat(historial.usuarioId()).isEqualTo(9);
        assertThat(historial.observacion()).isEqualTo("ok");
        assertThat(historial.fecha()).isNotNull();
    }

    @Test
    void listarDelegaEnElRepositorio() {
        List<CompraPedidoHistorial> esperado = List.of(
                new CompraPedidoHistorial(1L, 5, "BORRADOR", 1, null, null));
        when(repository.findByPedido(5)).thenReturn(esperado);
        ListCompraPedidoHistorialUseCaseImpl useCase = new ListCompraPedidoHistorialUseCaseImpl(repository);

        assertThat(useCase.listar(5)).isEqualTo(esperado);
    }

}
