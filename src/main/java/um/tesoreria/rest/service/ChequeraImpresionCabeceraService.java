/**
 * 
 */
package um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.kotlin.model.ChequeraImpresionCabecera;
import um.tesoreria.rest.repository.IChequeraImpresionCabeceraRepository;
import um.tesoreria.rest.repository.IChequeraImpresionCabeceraRepository;

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
