package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class MarcarRecepcionCompraPedidoUseCaseImplTest {

    @Mock private CompraPedidoRepository compraPedidoRepository;

    @Test
    void marcarRecepcionParcialTransicionaElPedido() {
        CompraPedido formalizado = CompraPedido.builder()
                .compraPedidoId(5).estado(CompraPedidoEstado.FORMALIZADO).build();
        when(compraPedidoRepository.findById(5)).thenReturn(Optional.of(formalizado));
        when(compraPedidoRepository.update(formalizado)).thenReturn(Optional.of(formalizado));

        CompraPedido marcado = new MarcarRecepcionParcialCompraPedidoUseCaseImpl(compraPedidoRepository).marcar(5);

        assertThat(marcado.getEstado()).isEqualTo(CompraPedidoEstado.RECEPCION_PARCIAL);
    }

    @Test
    void marcarRecepcionParcialFallaSiElPedidoNoExiste() {
        when(compraPedidoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new MarcarRecepcionParcialCompraPedidoUseCaseImpl(compraPedidoRepository)
                .marcar(99))
                .isInstanceOf(CompraPedidoException.class);
    }

    @Test
    void marcarRecepcionadoTransicionaElPedido() {
        CompraPedido parcial = CompraPedido.builder()
                .compraPedidoId(5).estado(CompraPedidoEstado.RECEPCION_PARCIAL).build();
        when(compraPedidoRepository.findById(5)).thenReturn(Optional.of(parcial));
        when(compraPedidoRepository.update(parcial)).thenReturn(Optional.of(parcial));

        CompraPedido marcado = new MarcarRecepcionadoCompraPedidoUseCaseImpl(compraPedidoRepository).marcar(5);

        assertThat(marcado.getEstado()).isEqualTo(CompraPedidoEstado.RECEPCIONADO);
    }

    @Test
    void marcarRecepcionadoFallaSiElPedidoNoExiste() {
        when(compraPedidoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new MarcarRecepcionadoCompraPedidoUseCaseImpl(compraPedidoRepository).marcar(99))
                .isInstanceOf(CompraPedidoException.class);
    }

}
