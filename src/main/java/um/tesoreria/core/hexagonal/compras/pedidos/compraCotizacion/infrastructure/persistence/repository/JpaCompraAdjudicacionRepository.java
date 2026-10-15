package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraAdjudicacionEntity;

import java.util.Optional;

public interface JpaCompraAdjudicacionRepository extends JpaRepository<CompraAdjudicacionEntity, Long> {

    Optional<CompraAdjudicacionEntity> findFirstByCompraCotizacionIdOrderByCompraAdjudicacionIdDesc(
            Long compraCotizacionId);
}
