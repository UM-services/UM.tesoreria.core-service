package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.correlativo.SecuenciaAnualRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionSecuenciaEntity;

@Repository
public interface JpaCompraFormalizacionSecuenciaRepository
        extends SecuenciaAnualRepository<CompraFormalizacionSecuenciaEntity> {
}
