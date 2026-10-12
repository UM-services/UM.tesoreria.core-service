package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.exception.CompraPedidoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.CompraPedidoRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarcarFormalizadoCompraPedidoUseCaseImplTest {

    @Mock private CompraPedidoRepository compraPedidoRepository;

    @InjectMocks
    private MarcarFormalizadoCompraPedidoUseCaseImpl useCase;

    @Test
    void marcaElPedidoAdjudicado() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5).estado(CompraPedidoEstado.ADJUDICADO).build();
        when(compraPedidoRepository.findById(5)).thenReturn(Optional.of(pedido));
        when(compraPedidoRepository.update(pedido)).thenReturn(Optional.of(pedido));

        assertThat(useCase.marcar(5).getEstado()).isEqualTo(CompraPedidoEstado.FORMALIZADO);
    }

    @Test
    void pedidoInexistenteFalla() {
        when(compraPedidoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.marcar(99)).isInstanceOf(CompraPedidoException.class);
    }

    @Test
    void pedidoNoAdjudicadoFalla() {
        CompraPedido pedido = CompraPedido.builder()
                .compraPedidoId(5).estado(CompraPedidoEstado.PENDIENTE_ADJUDICACION).build();
        when(compraPedidoRepository.findById(5)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> useCase.marcar(5)).isInstanceOf(IllegalStateException.class);
    }
}
