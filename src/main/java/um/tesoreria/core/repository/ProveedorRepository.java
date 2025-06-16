/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.Proveedor;

/**
 * @author daniel
 *
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

	Optional<Proveedor> findByProveedorId(Integer proveedorId);

	Optional<Proveedor> findTopByCuit(String cuit);

	Optional<Proveedor> findTopByOrderByProveedorIdDesc();

}
