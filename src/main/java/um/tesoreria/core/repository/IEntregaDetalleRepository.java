/**
 *
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.EntregaDetalle;

/**
 * @author daniel
 *
 */
@Repository
public interface IEntregaDetalleRepository extends JpaRepository<EntregaDetalle, Long> {

    List<EntregaDetalle> findAllByProveedorMovimientoIdOrderByOrden(Long proveedorMovimientoId);

    List<EntregaDetalle> findAllByProveedorMovimientoIdInOrderByOrden(List<Long> proveedorMovimientoIds);

    List<EntregaDetalle> findAllByEntregaIdOrderByOrden(Long entregaId);

    List<EntregaDetalle> findAllByProveedorMovimientoIdAndOrden(Long proveedorMovimientoId, Integer orden);

    List<EntregaDetalle> findAllByProveedorArticuloId(Long proveedorArticuloId);

    Optional<EntregaDetalle> findByEntregaDetalleId(Long entregaDetalleId);

    @Modifying
    void deleteByEntregaDetalleId(Long entregaDetalleId);

}
