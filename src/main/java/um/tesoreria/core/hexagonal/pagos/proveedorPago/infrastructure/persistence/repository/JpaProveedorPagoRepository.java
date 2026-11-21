package um.tesoreria.core.hexagonal.pagos.proveedorPago.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.infrastructure.persistence.entity.ProveedorPagoEntity;

import java.util.List;

@Repository
public interface JpaProveedorPagoRepository extends JpaRepository<ProveedorPagoEntity, Long> {

    List<ProveedorPagoEntity> findAllByProveedorMovimientoIdPago(Long proveedorMovimientoIdPago);

    List<ProveedorPagoEntity> findAllByProveedorMovimientoIdFactura(Long proveedorMovimientoIdFactura);
}
