/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ChequeraKey;
import um.tesoreria.core.repository.view.ChequeraKeyRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class ChequeraKeyService {

	private final ChequeraKeyRepository repository;

	public List<ChequeraKey> findAllByChequeraKey(List<String> chequeraKeys) {
		return repository.findAllByChequeraKeyInOrderByLectivoIdDesc(chequeraKeys);
	}

}
