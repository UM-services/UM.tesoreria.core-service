/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import um.tesoreria.core.model.view.ArticuloKey;

/**
 * @author daniel
 *
 */
public interface IArticuloKeyRepositoryCustom {

	public List<ArticuloKey> findAllByStrings(List<String> conditions);

}
