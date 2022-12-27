/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.List;

import ar.edu.um.tesoreria.rest.model.view.ProveedorSearch;

/**
 * @author daniel
 *
 */
public interface IProveedorSearchRepositoryCustom {

	public List<ProveedorSearch> findAllByStrings(List<String> conditions);

}
