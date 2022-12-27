/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.ProveedorSearch;

/**
 * @author daniel
 *
 */
@Repository
public interface IProveedorSearchRepository
		extends JpaRepository<ProveedorSearch, Integer>, IProveedorSearchRepositoryCustom {

}
