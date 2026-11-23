package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.correlativo.ReservaCorrelativoAnual;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionSecuenciaPort;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionSecuenciaEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository.JpaCompraCotizacionSecuenciaRepository;

/** Reserva del correlativo anual de cotizaciones con lock pesimista (ver {@link ReservaCorrelativoAnual}). */
@Component
@RequiredArgsConstructor
public class CompraCotizacionSecuenciaRepositoryAdapter implements CompraCotizacionSecuenciaPort {

    private final JpaCompraCotizacionSecuenciaRepository repository;
    private final ReservaCorrelativoAnual reserva;

    @Override
    public int reservarNumero(Integer ejercicioId, Integer anio) {
        return reserva.reservar(repository, ejercicioId,
                () -> CompraCotizacionSecuenciaEntity.builder()
                        .ejercicioId(ejercicioId)
                        .anio(anio)
                        .ultimoNumero(0)
                        .build());
    }

}
