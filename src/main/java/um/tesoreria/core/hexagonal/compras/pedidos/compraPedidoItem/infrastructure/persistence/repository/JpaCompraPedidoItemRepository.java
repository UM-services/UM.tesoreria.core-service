package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.entity.CompraPedidoItemEntity;

import java.util.List;

@Repository
public interface JpaCompraPedidoItemRepository extends JpaRepository<CompraPedidoItemEntity, Integer> {

    List<CompraPedidoItemEntity> findByCompraPedidoIdOrderByOrdenAsc(Integer compraPedidoId);

    void deleteByCompraPedidoId(Integer compraPedidoId);

}
