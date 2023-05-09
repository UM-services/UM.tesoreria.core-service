/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.ProveedorValor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IProveedorValorRepository extends JpaRepository<ProveedorValor, Long> {

	public List<ProveedorValor> findAllByProveedorMovimientoId(Long proveedorMovimientoId, Sort sort);

	public List<ProveedorValor> findAllByValorMovimientoId(Long valorMovimientoId);

	public Optional<ProveedorValor> findFirstByValorMovimientoId(Long valorMovimientoId);

	public Optional<ProveedorValor> findFirstByProveedorMovimientoIdOrderByOrdenDesc(Long proveedorMovimientoId);

	public Optional<ProveedorValor> findByProveedorValorId(Long proveedorValorId);

	@Modifying
	public void deleteByProveedorValorId(Long proveedorValorId);

}
