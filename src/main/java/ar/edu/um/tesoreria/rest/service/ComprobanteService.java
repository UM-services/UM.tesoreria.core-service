/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ComprobanteNotFoundException;
import ar.edu.um.tesoreria.rest.model.Comprobante;
import ar.edu.um.tesoreria.rest.repository.IComprobanteRepository;

/**
 * @author daniel
 *
 */
@Service
public class ComprobanteService {

	@Autowired
	private IComprobanteRepository repository;

	public List<Comprobante> findAll() {
		return repository.findAll();
	}

	public List<Comprobante> findAllByTipoTransaccionId(Integer tipoTransaccionId) {
		return repository.findAllByTipoTransaccionId(tipoTransaccionId);
	}

	public Comprobante findByComprobanteId(Integer comprobanteId) {
		return repository.findByComprobanteId(comprobanteId)
				.orElseThrow(() -> new ComprobanteNotFoundException(comprobanteId));
	}

}
