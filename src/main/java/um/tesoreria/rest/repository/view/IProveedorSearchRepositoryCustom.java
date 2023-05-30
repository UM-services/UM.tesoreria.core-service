/**
 * 
 */
package um.tesoreria.rest.repository.view;

import java.util.List;

import um.tesoreria.rest.model.view.ProveedorSearch;

/**
 * @author daniel
 *
 */
public interface IProveedorSearchRepositoryCustom {

	public List<ProveedorSearch> findAllByStrings(List<String> conditions);

}
