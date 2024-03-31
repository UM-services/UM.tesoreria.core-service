/**
 * 
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;

/**
 * @author daniel
 *
 */
@Repository
public interface IProveedorArticuloRepository extends JpaRepository<ProveedorArticulo, Long> {

	public List<ProveedorArticulo> findAllByProveedorMovimientoIdInOrderByOrden(List<Long> proveedorMovimientoIds);

	public List<ProveedorArticulo> findAllByProveedorMovimientoIdInAndEntregadoOrderByOrden(
			List<Long> proveedorMovimientoIds, BigDecimal entregado);

	public List<ProveedorArticulo> findAllByProveedorMovimientoIdOrderByOrden(Long proveedorMovimientoId);

	public List<ProveedorArticulo> findAllByProveedorMovimientoIdAndEntregadoOrderByOrden(Long proveedorMovimientoId,
			BigDecimal entregado);

	public Optional<ProveedorArticulo> findByProveedorArticuloId(Long proveedorArticuloId);

	@Modifying
    public void deleteByProveedorArticuloId(Long proveedorArticuloId);
}
