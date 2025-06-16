/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.IngresoPeriodo;
import um.tesoreria.core.model.view.pk.IngresoPeriodoPk;

/**
 * @author daniel
 *
 */
@Repository
public interface IngresoPeriodoRepository extends JpaRepository<IngresoPeriodo, IngresoPeriodoPk> {

	public List<IngresoPeriodo> findAllByAnhoAndMes(Integer anho, Integer mes, Sort sort);

}
