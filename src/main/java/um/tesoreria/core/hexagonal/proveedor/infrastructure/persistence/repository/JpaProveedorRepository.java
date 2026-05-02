/**
 * 
 */
package um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.entity.ProveedorEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaProveedorRepository extends JpaRepository<ProveedorEntity, Integer> {

	Optional<ProveedorEntity> findByProveedorId(Integer proveedorId);

	Optional<ProveedorEntity> findTopByCuit(String cuit);

	Optional<ProveedorEntity> findTopByOrderByProveedorIdDesc();

}
