package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.domain.ports.in.ReservarNumeroCompraPedidoUseCase;

/**
 * Fachada del correlativo anual del pedido de compra.
 */
@Service
@RequiredArgsConstructor
public class CompraPedidoSecuenciaService {

    private final ReservarNumeroCompraPedidoUseCase reservarNumeroCompraPedidoUseCase;

    public int reservarNumero(Integer ejercicioId, Integer anio) {
        return reservarNumeroCompraPedidoUseCase.reservarNumero(ejercicioId, anio);
    }

}
