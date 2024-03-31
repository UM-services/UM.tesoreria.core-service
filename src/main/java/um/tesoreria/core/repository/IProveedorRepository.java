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
public interface IProveedorRepository extends JpaRepository<Proveedor, Integer> {

	public Optional<Proveedor> findByProveedorId(Integer proveedorId);

	public Optional<Proveedor> findTopByCuit(String cuit);

	public Optional<Proveedor> findTopByOrderByProveedorIdDesc();

}
