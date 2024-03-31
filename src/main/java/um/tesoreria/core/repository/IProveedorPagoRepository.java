package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ProveedorPago;

import java.util.List;

@Repository
public interface IProveedorPagoRepository extends JpaRepository<ProveedorPago, Long> {

    public List<ProveedorPago> findAllByProveedorMovimientoIdPago(Long proveedorMovimientoId);

    @Modifying
    public void deleteByProveedorPagoId(Long proveedorPagoId);

}
