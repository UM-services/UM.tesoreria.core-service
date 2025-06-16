/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ProveedorComprobante;

/**
 * @author daniel
 *
 */
@Repository
public interface ProveedorComprobanteRepository extends JpaRepository<ProveedorComprobante, Long> {

	public List<ProveedorComprobante> findAllByProveedorMovimientoIdOrdenPago(Long proveedorMovimientoId);

}
