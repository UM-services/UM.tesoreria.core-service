package um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.entity.EntregaDetalleEntity;

import java.util.List;

@Repository
public interface JpaEntregaDetalleRepository extends JpaRepository<EntregaDetalleEntity, Long> {

    List<EntregaDetalleEntity> findAllByProveedorMovimientoIdInOrderByProveedorMovimientoIdAscOrdenAsc(
            List<Long> proveedorMovimientoIds);
}
