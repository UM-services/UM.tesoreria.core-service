/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.IngresoPeriodo;
import ar.edu.um.tesoreria.rest.model.view.pk.IngresoPeriodoPk;

/**
 * @author daniel
 *
 */
@Repository
public interface IIngresoPeriodoRepository extends JpaRepository<IngresoPeriodo, IngresoPeriodoPk> {

	public List<IngresoPeriodo> findAllByAnhoAndMes(Integer anho, Integer mes, Sort sort);

}
