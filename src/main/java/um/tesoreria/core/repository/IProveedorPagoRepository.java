package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ProveedorPago;

import java.util.List;

@Repository
public interface IProveedorPagoRepository extends JpaRepository<ProveedorPago, Long> {

    List<ProveedorPago> findAllByProveedorMovimientoIdPago(Long proveedorMovimientoId);

    List<ProveedorPago> findAllByProveedorMovimientoIdFactura(Long proveedorMovimientoId);

    @Modifying
    void deleteByProveedorPagoId(Long proveedorPagoId);

}
