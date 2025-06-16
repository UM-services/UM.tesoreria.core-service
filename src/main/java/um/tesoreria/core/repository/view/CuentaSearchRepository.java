/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.CuentaSearch;

/**
 * @author daniel
 *
 */
@Repository
public interface CuentaSearchRepository extends JpaRepository<CuentaSearch, BigDecimal>, CuentaSearchRepositoryCustom {

}
