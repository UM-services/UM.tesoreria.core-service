/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.repository.IChequeraPagoAsientoRepository;

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
