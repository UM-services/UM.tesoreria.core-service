package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.persistence.entity.CompraPedidoHistorialEntity;

import java.util.List;

@Repository
public interface JpaCompraPedidoHistorialRepository extends JpaRepository<CompraPedidoHistorialEntity, Long> {

    List<CompraPedidoHistorialEntity> findByCompraPedidoIdOrderByFechaAsc(Integer compraPedidoId);

}
