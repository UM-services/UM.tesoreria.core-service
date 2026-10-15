package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionItemEntity;

import java.util.List;

@Repository
public interface JpaCompraFormalizacionItemRepository extends JpaRepository<CompraFormalizacionItemEntity, Long> {

    List<CompraFormalizacionItemEntity> findByCompraFormalizacionIdOrderByOrden(Long compraFormalizacionId);

    void deleteByCompraFormalizacionId(Long compraFormalizacionId);

}
