package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.entity.CompraPedidoAutorizanteDependenciaEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.entity.CompraPedidoAutorizanteDependenciaId;

import java.util.List;

@Repository
public interface JpaCompraPedidoAutorizanteDependenciaRepository
        extends JpaRepository<CompraPedidoAutorizanteDependenciaEntity, CompraPedidoAutorizanteDependenciaId> {

    List<CompraPedidoAutorizanteDependenciaEntity> findAllByAutorizanteIdOrderByDependenciaIdAsc(Integer autorizanteId);

    boolean existsByAutorizanteIdAndDependenciaId(Integer autorizanteId, Integer dependenciaId);

    void deleteByAutorizanteIdAndDependenciaId(Integer autorizanteId, Integer dependenciaId);

}
