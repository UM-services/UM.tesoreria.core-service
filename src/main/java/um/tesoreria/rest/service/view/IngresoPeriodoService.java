/**
 * 
 */
package um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.model.view.IngresoPeriodo;
import um.tesoreria.rest.repository.view.IIngresoPeriodoRepository;
import um.tesoreria.rest.repository.view.IIngresoPeriodoRepository;

/**
 * @author daniel
 *
 */
@Service
public class IngresoPeriodoService {
	
	@Autowired
	private IIngresoPeriodoRepository repository;

	public List<IngresoPeriodo> findAllByPeriodo(Integer anho, Integer mes) {
		return repository.findAllByAnhoAndMes(anho, mes, Sort.by("facultadId").ascending().and(Sort.by("geograficaId").ascending().and(Sort.by("tipopagoId").ascending())));
	}

}
