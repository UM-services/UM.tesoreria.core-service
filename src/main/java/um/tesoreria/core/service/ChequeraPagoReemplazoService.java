/**
 * 
 */
package um.tesoreria.core.service;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraPagoReemplazoException;
import um.tesoreria.core.model.ChequeraPagoReemplazo;
import um.tesoreria.core.repository.ChequeraPagoReemplazoRepository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraPagoReemplazoService {

	private final ChequeraPagoReemplazoRepository repository;

	public ChequeraPagoReemplazoService(ChequeraPagoReemplazoRepository repository) {
		this.repository = repository;
	}

	public List<ChequeraPagoReemplazo> findAllByTipoPagoIdAndFechaAcreditacion(Integer tipoPagoId, OffsetDateTime fecha) {
		return repository.findAllByTipoPagoIdAndAcreditacion(tipoPagoId, fecha);
	}

	public ChequeraPagoReemplazo findByChequeraPagoReemplazoId(Long chequeraPagoReemplazoId) {
		return repository.findByChequeraPagoReemplazoId(chequeraPagoReemplazoId).orElseThrow(() -> new ChequeraPagoReemplazoException(chequeraPagoReemplazoId));
	}

}
