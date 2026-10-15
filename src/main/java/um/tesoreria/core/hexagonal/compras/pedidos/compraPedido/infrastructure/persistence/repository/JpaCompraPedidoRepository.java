package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.entity.CompraPedidoEntity;

import java.util.Optional;

@Repository
public interface JpaCompraPedidoRepository
        extends JpaRepository<CompraPedidoEntity, Integer>, JpaSpecificationExecutor<CompraPedidoEntity> {

    Optional<CompraPedidoEntity> findByNumero(String numero);

}
