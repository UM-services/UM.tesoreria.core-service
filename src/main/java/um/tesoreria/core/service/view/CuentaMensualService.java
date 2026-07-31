/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.CuentaMensual;
import um.tesoreria.core.repository.view.CuentaMensualRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class CuentaMensualService {
	private final CuentaMensualRepository repository;
	
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
