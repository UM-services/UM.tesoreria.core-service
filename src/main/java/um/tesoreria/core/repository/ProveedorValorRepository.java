/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ProveedorValor;

/**
 * @author daniel
 *
 */
@Repository
public interface ProveedorValorRepository extends JpaRepository<ProveedorValor, Long> {

	List<ProveedorValor> findAllByProveedorMovimientoId(Long proveedorMovimientoId, Sort sort);

	List<ProveedorValor> findAllByValorMovimientoId(Long valorMovimientoId);

	Optional<ProveedorValor> findFirstByValorMovimientoId(Long valorMovimientoId);

	Optional<ProveedorValor> findFirstByProveedorMovimientoIdOrderByOrdenDesc(Long proveedorMovimientoId);

	Optional<ProveedorValor> findByProveedorValorId(Long proveedorValorId);

	@Modifying
	void deleteByProveedorValorId(Long proveedorValorId);

	@Modifying
	void deleteAllByProveedorValorIdIn(List<Long> proveedorValorIds);

}
