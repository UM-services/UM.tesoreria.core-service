/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.ProveedorComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IProveedorComprobanteRepository extends JpaRepository<ProveedorComprobante, Long> {

	public List<ProveedorComprobante> findAllByProveedorMovimientoIdOrdenPago(Long proveedorMovimientoId);

}
