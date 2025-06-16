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
public interface ArticuloKeyRepositoryCustom {

	public List<ArticuloKey> findAllByStrings(List<String> conditions);

}
