/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ChequeraKey;
import um.tesoreria.core.repository.view.ChequeraKeyRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraKeyService {

	@Autowired
	private ChequeraKeyRepository repository;

	public List<ChequeraKey> findAllByChequeraKey(List<String> chequeraKeys) {
		return repository.findAllByChequeraKeyInOrderByLectivoIdDesc(chequeraKeys);
	}

}
