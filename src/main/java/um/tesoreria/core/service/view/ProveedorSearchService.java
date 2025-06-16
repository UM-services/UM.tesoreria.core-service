/**
 *
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ProveedorSearch;
import um.tesoreria.core.repository.view.ProveedorSearchRepository;

/**
 * @author daniel
 *
 */
@Service
public class ProveedorSearchService {

    private final ProveedorSearchRepository repository;

    public ProveedorSearchService(ProveedorSearchRepository repository) {
        this.repository = repository;
    }

    public List<ProveedorSearch> findAllByStrings(List<String> conditions) {
        return repository.findAllByStrings(conditions);
    }

}
