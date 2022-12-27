/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.ChequeraKey;
import ar.edu.um.tesoreria.rest.repository.view.IChequeraKeyRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraKeyService {

	@Autowired
	private IChequeraKeyRepository repository;

	public List<ChequeraKey> findAllByChequeraKey(List<String> chequeraKeys) {
		return repository.findAllByChequeraKeyInOrderByLectivoIdDesc(chequeraKeys);
	}

}
