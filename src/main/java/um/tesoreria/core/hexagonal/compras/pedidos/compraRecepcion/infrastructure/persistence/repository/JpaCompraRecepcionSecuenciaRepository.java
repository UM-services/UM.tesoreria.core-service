package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.correlativo.SecuenciaAnualRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity.CompraRecepcionSecuenciaEntity;

@Repository
public interface JpaCompraRecepcionSecuenciaRepository
        extends SecuenciaAnualRepository<CompraRecepcionSecuenciaEntity> {
}
