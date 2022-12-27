/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.EntregaDetalle;

/**
 * @author daniel
 *
 */
@Repository
public interface IEntregaDetalleRepository extends JpaRepository<EntregaDetalle, Long> {

	public List<EntregaDetalle> findAllByProveedorMovimientoIdOrderByOrden(Long proveedorMovimientoId);

	public List<EntregaDetalle> findAllByEntregaIdOrderByOrden(Long entregaId);

	public Optional<EntregaDetalle> findByEntregaDetalleId(Long entregaDetalleId);

	@Modifying
	public void deleteByEntregaDetalleId(Long entregaDetalleId);

}
