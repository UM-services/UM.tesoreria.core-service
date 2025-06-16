/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import um.tesoreria.core.model.view.CuentaSearch;

/**
 * @author daniel
 *
 */
public interface CuentaSearchRepositoryCustom {

	public List<CuentaSearch> findAllByStrings(List<String> conditions, Boolean visible);

}
