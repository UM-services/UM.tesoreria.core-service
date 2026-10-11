package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionEntity;

public interface JpaCompraCotizacionRepository extends JpaRepository<CompraCotizacionEntity, Long>,
        JpaSpecificationExecutor<CompraCotizacionEntity> {
}
