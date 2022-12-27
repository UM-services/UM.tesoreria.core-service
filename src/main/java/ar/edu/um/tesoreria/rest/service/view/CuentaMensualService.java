/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.CuentaMensual;
import ar.edu.um.tesoreria.rest.repository.view.ICuentaMensualRepository;

/**
 * @author daniel
 *
 */
@Service
public class CuentaMensualService {
	@Autowired
	private ICuentaMensualRepository repository;
	
	public List<CuentaMensual> findIngresosByMes(Integer anho, Integer mes) {
		return repository.findIngresosByMes(anho, mes);
	}

	public List<CuentaMensual> findGastosByMes(Integer anho, Integer mes) {
		return repository.findGastosByMes(anho, mes);
	}

	public List<CuentaMensual> findBienesUsoByMes(Integer anho, Integer mes) {
		return repository.findBienesUsoByMes(anho, mes);
	}
}
