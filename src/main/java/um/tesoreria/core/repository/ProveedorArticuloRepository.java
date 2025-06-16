/**
 * 
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;

/**
 * @author daniel
 *
 */
@Repository
public interface ProveedorArticuloRepository extends JpaRepository<ProveedorArticulo, Long> {

	List<ProveedorArticulo> findAllByProveedorMovimientoIdInOrderByOrden(List<Long> proveedorMovimientoIds);

	List<ProveedorArticulo> findAllByProveedorMovimientoIdInAndEntregadoOrderByOrden(
			List<Long> proveedorMovimientoIds, BigDecimal entregado);

	List<ProveedorArticulo> findAllByProveedorMovimientoIdOrderByOrden(Long proveedorMovimientoId);

	List<ProveedorArticulo> findAllByProveedorMovimientoIdAndEntregadoOrderByOrden(Long proveedorMovimientoId,
			BigDecimal entregado);

	Optional<ProveedorArticulo> findByProveedorArticuloId(Long proveedorArticuloId);

	@Modifying
    void deleteByProveedorArticuloId(Long proveedorArticuloId);

	@Modifying
	@Query("DELETE FROM ProveedorArticulo a WHERE a.proveedorArticuloId IN :ids")
	void deleteAllByProveedorArticuloIdIn(List<Long> ids);

}
