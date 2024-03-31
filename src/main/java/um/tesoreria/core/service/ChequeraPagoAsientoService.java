/**
 * 
 */
package um.tesoreria.core.service;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.repository.IChequeraPagoAsientoRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraPagoAsientoService {

	@Autowired
	private IChequeraPagoAsientoRepository repository;

	@Transactional
	public void deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(Integer facultadId, Integer tipochequeraId,
			Long chequeraserieId) {
		repository.deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(facultadId, tipochequeraId,
				chequeraserieId);
	}

}
