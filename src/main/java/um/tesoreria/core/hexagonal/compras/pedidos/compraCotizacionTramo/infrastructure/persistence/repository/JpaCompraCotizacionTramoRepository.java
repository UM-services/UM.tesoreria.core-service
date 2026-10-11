package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.entity.CompraCotizacionTramoEntity;

import java.util.Optional;

@Repository
public interface JpaCompraCotizacionTramoRepository extends JpaRepository<CompraCotizacionTramoEntity, Long> {

    Optional<CompraCotizacionTramoEntity> findByCotizacionTramoId(Long cotizacionTramoId);

    boolean existsByCotizacionTramoId(Long cotizacionTramoId);

    void deleteByCotizacionTramoId(Long cotizacionTramoId);

}
