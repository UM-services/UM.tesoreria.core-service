/**
 * 
 */
package um.tesoreria.core.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.ArticuloKey;

/**
 * @author daniel
 *
 */
@Repository
public interface IArticuloKeyRepository extends JpaRepository<ArticuloKey, Long>, IArticuloKeyRepositoryCustom {

}
