package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;

import java.math.BigDecimal;

public interface EstimarCompraPedidoUseCase {

    CompraPedido estimar(Integer compraPedidoId, BigDecimal monto, String fuente);

}
