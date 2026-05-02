/**
 * 
 */
package um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.repository;

import java.util.List;

import um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.entity.ProveedorSearchEntity;

/**
 * @author daniel
 *
 */
public interface JpaProveedorSearchRepositoryCustom {

	List<ProveedorSearchEntity> findAllByStrings(List<String> conditions);

}
