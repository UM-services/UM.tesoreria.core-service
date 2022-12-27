/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.CuentaMovimientoAsiento;
import ar.edu.um.tesoreria.rest.repository.view.ICuentaMovimientoAsientoRepository;

/**
 * @author daniel
 *
 */
@Service
public class CuentaMovimientoAsientoService {

	@Autowired
	private ICuentaMovimientoAsientoRepository repository;

	public List<CuentaMovimientoAsiento> findAllByAsientoIn(List<String> asientos, Sort sort) {
		return repository.findAllByAsientoIn(asientos, sort);
	}

}
