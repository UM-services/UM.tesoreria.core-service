/**
 * 
 */
package um.tesoreria.rest.repository.view;

import java.util.List;

import um.tesoreria.rest.model.view.ArticuloKey;

/**
 * @author daniel
 *
 */
public interface IArticuloKeyRepositoryCustom {

	public List<ArticuloKey> findAllByStrings(List<String> conditions);

}
