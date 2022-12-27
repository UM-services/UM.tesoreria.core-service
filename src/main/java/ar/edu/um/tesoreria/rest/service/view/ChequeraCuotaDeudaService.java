/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.ChequeraCuotaDeuda;
import ar.edu.um.tesoreria.rest.repository.view.IChequeraCuotaDeudaRepository;
import ar.edu.um.tesoreria.rest.util.Tool;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraCuotaDeudaService {

	@Autowired
	private IChequeraCuotaDeudaRepository repository;

	public List<ChequeraCuotaDeuda> findAllByRango(OffsetDateTime desde, OffsetDateTime hasta, Pageable pageable) {
		return repository.findAllByVencimiento1Between(Tool.firstTime(desde), Tool.lastTime(hasta), pageable);
	}

}
