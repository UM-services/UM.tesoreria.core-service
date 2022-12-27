/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.List;

import ar.edu.um.tesoreria.rest.model.view.CuentaSearch;

/**
 * @author daniel
 *
 */
public interface ICuentaSearchRepositoryCustom {

	public List<CuentaSearch> findAllByStrings(List<String> conditions, Boolean visible);

}
