/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.CuentaSearch;

/**
 * @author daniel
 *
 */
@Repository
public interface ICuentaSearchRepository extends JpaRepository<CuentaSearch, BigDecimal>, ICuentaSearchRepositoryCustom {

}
