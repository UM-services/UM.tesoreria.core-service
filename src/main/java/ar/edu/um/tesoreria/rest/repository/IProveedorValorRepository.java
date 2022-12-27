/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.ProveedorValor;

/**
 * @author daniel
 *
 */
@Repository
public interface IProveedorValorRepository extends JpaRepository<ProveedorValor, Long> {

	public List<ProveedorValor> findAllByProveedorMovimientoId(Long proveedorMovimientoId);

	public Optional<ProveedorValor> findByValorMovimientoId(Long valorMovimientoId);

	public Optional<ProveedorValor> findByProveedorValorId(Long proveedorValorId);

}
