/**
 * 
 */
package um.tesoreria.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.ChequeraImpresionCabecera;
import um.tesoreria.core.repository.IChequeraImpresionCabeceraRepository;

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
