/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.ArticuloKey;

/**
 * @author daniel
 *
 */
@Repository
public interface IArticuloKeyRepository extends JpaRepository<ArticuloKey, Long>, IArticuloKeyRepositoryCustom {

}
