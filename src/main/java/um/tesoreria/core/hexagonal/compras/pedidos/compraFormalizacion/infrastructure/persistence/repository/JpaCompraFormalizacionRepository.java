package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionEntity;

import java.util.Optional;

@Repository
public interface JpaCompraFormalizacionRepository
        extends JpaRepository<CompraFormalizacionEntity, Long>, JpaSpecificationExecutor<CompraFormalizacionEntity> {

    Optional<CompraFormalizacionEntity> findByCompraAdjudicacionId(Long compraAdjudicacionId);

    boolean existsByCompraAdjudicacionId(Long compraAdjudicacionId);

}
