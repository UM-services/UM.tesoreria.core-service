package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionItemEntity;

import java.util.List;

public interface JpaCompraCotizacionItemRepository extends JpaRepository<CompraCotizacionItemEntity, Long> {

    List<CompraCotizacionItemEntity> findByCompraCotizacionIdOrderByOrden(Long compraCotizacionId);

    void deleteByCompraCotizacionId(Long compraCotizacionId);
}
