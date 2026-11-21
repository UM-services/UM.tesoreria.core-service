package um.tesoreria.core.hexagonal.compras.proveedorArticulo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.infrastructure.persistence.entity.ProveedorArticuloEntity;

import java.util.List;

@Repository
public interface JpaProveedorArticuloRepository extends JpaRepository<ProveedorArticuloEntity, Long> {

    List<ProveedorArticuloEntity> findAllByProveedorMovimientoIdOrderByOrdenAsc(Long proveedorMovimientoId);

    List<ProveedorArticuloEntity> findAllByProveedorMovimientoIdInOrderByProveedorMovimientoIdAscOrdenAsc(
            List<Long> proveedorMovimientoIds);
}
