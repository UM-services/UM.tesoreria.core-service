package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.correlativo.ReservaCorrelativoAnual;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.domain.ports.out.CompraPedidoSecuenciaRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.infrastructure.persistence.entity.CompraPedidoSecuenciaEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.infrastructure.persistence.repository.JpaCompraPedidoSecuenciaRepository;

/** Reserva del correlativo anual del pedido con lock pesimista (ver {@link ReservaCorrelativoAnual}). */
@Component
@RequiredArgsConstructor
public class CompraPedidoSecuenciaRepositoryAdapter implements CompraPedidoSecuenciaRepository {

    private final JpaCompraPedidoSecuenciaRepository repository;
    private final ReservaCorrelativoAnual reserva;

    @Override
    public int reservarSiguienteNumero(Integer ejercicioId, Integer anio) {
        return reserva.reservar(repository, ejercicioId,
                () -> CompraPedidoSecuenciaEntity.builder()
                        .ejercicioId(ejercicioId)
                        .anio(anio)
                        .ultimoNumero(0)
                        .build());
    }

}
