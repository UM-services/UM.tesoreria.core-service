/**
 * 
 */
package um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.repository;

import java.util.List;

import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.entity.ArticuloKey;

/**
 * @author daniel
 *
 */
public interface ArticuloKeyRepositoryCustom {

	public List<ArticuloKey> findAllByStrings(List<String> conditions);

}
