package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.application.exception.CompraPedidoSecuenciaException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.domain.ports.in.ReservarNumeroCompraPedidoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.domain.ports.out.CompraPedidoSecuenciaRepository;

@Component
@RequiredArgsConstructor
public class ReservarNumeroCompraPedidoUseCaseImpl implements ReservarNumeroCompraPedidoUseCase {

    private final CompraPedidoSecuenciaRepository compraPedidoSecuenciaRepository;

    @Override
    public int reservarNumero(Integer ejercicioId, Integer anio) {
        if (ejercicioId == null) {
            throw new CompraPedidoSecuenciaException("El correlativo del pedido requiere el ejercicio");
        }
        return compraPedidoSecuenciaRepository.reservarSiguienteNumero(ejercicioId, anio);
    }

}
