package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionProveedorItemEntity;

import java.util.List;

public interface JpaCompraCotizacionProveedorItemRepository extends JpaRepository<CompraCotizacionProveedorItemEntity, Long> {

    List<CompraCotizacionProveedorItemEntity> findByCompraCotizacionProveedorId(Long compraCotizacionProveedorId);

    void deleteByCompraCotizacionProveedorId(Long compraCotizacionProveedorId);
}
