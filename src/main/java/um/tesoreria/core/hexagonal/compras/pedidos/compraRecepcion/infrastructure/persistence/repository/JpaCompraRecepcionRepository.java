package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity.CompraRecepcionEntity;

import java.util.List;

@Repository
public interface JpaCompraRecepcionRepository
        extends JpaRepository<CompraRecepcionEntity, Long>, JpaSpecificationExecutor<CompraRecepcionEntity> {

    List<CompraRecepcionEntity> findByCompraFormalizacionIdOrderByFecha(Long compraFormalizacionId);

}
