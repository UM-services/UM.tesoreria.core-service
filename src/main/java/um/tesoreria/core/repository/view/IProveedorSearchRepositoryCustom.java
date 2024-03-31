/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import um.tesoreria.core.model.view.ProveedorSearch;

/**
 * @author daniel
 *
 */
public interface IProveedorSearchRepositoryCustom {

	public List<ProveedorSearch> findAllByStrings(List<String> conditions);

}
