package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.correlativo.ReservaCorrelativoAnual;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out.CompraFormalizacionSecuenciaPort;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionSecuenciaEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.repository.JpaCompraFormalizacionSecuenciaRepository;

/** Reserva del correlativo anual de órdenes de compra con lock pesimista (ver {@link ReservaCorrelativoAnual}). */
@Component
@RequiredArgsConstructor
public class CompraFormalizacionSecuenciaRepositoryAdapter implements CompraFormalizacionSecuenciaPort {

    private final JpaCompraFormalizacionSecuenciaRepository repository;
    private final ReservaCorrelativoAnual reserva;

    @Override
    public int reservarNumero(Integer ejercicioId, Integer anio) {
        return reserva.reservar(repository, ejercicioId,
                () -> CompraFormalizacionSecuenciaEntity.builder()
                        .ejercicioId(ejercicioId)
                        .anio(anio)
                        .ultimoNumero(0)
                        .build());
    }

}
