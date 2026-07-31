/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.DomicilioKey;
import um.tesoreria.core.repository.view.DomicilioKeyRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class DomicilioKeyService {

	private final DomicilioKeyRepository repository;

	public List<DomicilioKey> findAllByUnifiedIn(List<String> keys) {
		return repository.findAllByUnifiedIn(keys);
	}

}
