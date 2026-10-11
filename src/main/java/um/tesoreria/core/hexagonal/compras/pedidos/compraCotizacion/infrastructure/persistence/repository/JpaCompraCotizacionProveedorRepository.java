package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionProveedorEntity;

import java.util.List;

public interface JpaCompraCotizacionProveedorRepository extends JpaRepository<CompraCotizacionProveedorEntity, Long> {

    List<CompraCotizacionProveedorEntity> findByCompraCotizacionId(Long compraCotizacionId);

    void deleteByCompraCotizacionId(Long compraCotizacionId);
}
