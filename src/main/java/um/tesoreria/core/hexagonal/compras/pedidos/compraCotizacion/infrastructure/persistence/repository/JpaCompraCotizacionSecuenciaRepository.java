package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.correlativo.SecuenciaAnualRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionSecuenciaEntity;

@Repository
public interface JpaCompraCotizacionSecuenciaRepository
        extends SecuenciaAnualRepository<CompraCotizacionSecuenciaEntity> {
}
