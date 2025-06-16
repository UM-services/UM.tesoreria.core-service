/**
 * 
 */
package um.tesoreria.core.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.ProveedorSearch;

/**
 * @author daniel
 *
 */
@Repository
public interface ProveedorSearchRepository extends JpaRepository<ProveedorSearch, Integer>, ProveedorSearchRepositoryCustom {

}
