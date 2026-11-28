/**
 *
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.ChequeraImpresionDetalle;
import um.tesoreria.core.repository.ChequeraImpresionDetalleRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class ChequeraImpresionDetalleService {

	private final ChequeraImpresionDetalleRepository repository;

	public List<ChequeraImpresionDetalle> saveAll(List<ChequeraImpresionDetalle> detalles) {
		return repository.saveAll(detalles);
	}

	public List<ChequeraImpresionDetalle> findAllByCabeceraId(Long chequeraImpresionCabeceraId) {
		return repository.findAllByChequeraImpresionCabeceraId(chequeraImpresionCabeceraId);
	}

	public void deleteAllByCabeceraIds(Collection<Long> chequeraImpresionCabeceraIds) {
		if (chequeraImpresionCabeceraIds == null || chequeraImpresionCabeceraIds.isEmpty()) {
			return;
		}
		repository.deleteAllByChequeraImpresionCabeceraIdIn(chequeraImpresionCabeceraIds);
	}

}
