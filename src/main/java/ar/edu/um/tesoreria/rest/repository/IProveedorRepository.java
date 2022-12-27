/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Proveedor;

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
