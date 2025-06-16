/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.CuentaMovimientoAsiento;
import um.tesoreria.core.repository.view.CuentaMovimientoAsientoRepository;

/**
 * @author daniel
 *
 */
@Service
public class CuentaMovimientoAsientoService {

	@Autowired
	private CuentaMovimientoAsientoRepository repository;

	public List<CuentaMovimientoAsiento> findAllByAsientoIn(List<String> asientos, Sort sort) {
		return repository.findAllByAsientoIn(asientos, sort);
	}

}
