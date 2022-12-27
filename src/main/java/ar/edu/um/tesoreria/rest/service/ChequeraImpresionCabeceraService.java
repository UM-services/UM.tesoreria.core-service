/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.ChequeraImpresionCabecera;
import ar.edu.um.tesoreria.rest.repository.IChequeraImpresionCabeceraRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraImpresionCabeceraService {

	@Autowired
	private IChequeraImpresionCabeceraRepository repository;

	public ChequeraImpresionCabecera add(ChequeraImpresionCabecera cabecera) {
		cabecera = repository.save(cabecera);
		return cabecera;
	}

}
