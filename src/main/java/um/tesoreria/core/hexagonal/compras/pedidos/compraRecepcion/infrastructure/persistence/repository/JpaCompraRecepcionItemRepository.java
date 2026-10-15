package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity.CompraRecepcionItemEntity;

import java.util.List;

@Repository
public interface JpaCompraRecepcionItemRepository extends JpaRepository<CompraRecepcionItemEntity, Long> {

    List<CompraRecepcionItemEntity> findByCompraRecepcionIdOrderByOrden(Long compraRecepcionId);

    void deleteByCompraRecepcionId(Long compraRecepcionId);

}
