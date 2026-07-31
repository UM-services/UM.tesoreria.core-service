/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.CuentaMovimientoAsiento;
import um.tesoreria.core.repository.view.CuentaMovimientoAsientoRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class CuentaMovimientoAsientoService {

	private final CuentaMovimientoAsientoRepository repository;

	public List<CuentaMovimientoAsiento> findAllByAsientoIn(List<String> asientos, Sort sort) {
		return repository.findAllByAsientoIn(asientos, sort);
	}

}
