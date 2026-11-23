package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.correlativo.ReservaCorrelativoAnual;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionSecuenciaPort;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity.CompraRecepcionSecuenciaEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.repository.JpaCompraRecepcionSecuenciaRepository;

/** Reserva del correlativo anual de recepciones con lock pesimista (ver {@link ReservaCorrelativoAnual}). */
@Component
@RequiredArgsConstructor
public class CompraRecepcionSecuenciaRepositoryAdapter implements CompraRecepcionSecuenciaPort {

    private final JpaCompraRecepcionSecuenciaRepository repository;
    private final ReservaCorrelativoAnual reserva;

    @Override
    public int reservarNumero(Integer ejercicioId, Integer anio) {
        return reserva.reservar(repository, ejercicioId,
                () -> CompraRecepcionSecuenciaEntity.builder()
                        .ejercicioId(ejercicioId)
                        .anio(anio)
                        .ultimoNumero(0)
                        .build());
    }

}
