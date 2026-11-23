package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.correlativo.SecuenciaAnualRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.infrastructure.persistence.entity.CompraPedidoSecuenciaEntity;

@Repository
public interface JpaCompraPedidoSecuenciaRepository
        extends SecuenciaAnualRepository<CompraPedidoSecuenciaEntity> {
}
