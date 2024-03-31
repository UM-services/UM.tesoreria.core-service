/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.IngresoPeriodo;
import um.tesoreria.core.repository.view.IIngresoPeriodoRepository;

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
