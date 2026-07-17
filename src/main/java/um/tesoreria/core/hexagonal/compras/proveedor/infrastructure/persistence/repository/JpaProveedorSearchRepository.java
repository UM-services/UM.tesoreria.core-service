/**
 * 
 */
package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.entity.ProveedorSearchEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaProveedorSearchRepository extends JpaRepository<ProveedorSearchEntity, Integer>, JpaProveedorSearchRepositoryCustom {

}
