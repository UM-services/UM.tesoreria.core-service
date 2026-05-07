/**
 *
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.entity.ProveedorSearchEntity;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.repository.JpaProveedorSearchRepository;

/**
 * @author daniel
 *
 */
@Service
public class ProveedorSearchService {

    private final JpaProveedorSearchRepository repository;

    public ProveedorSearchService(JpaProveedorSearchRepository repository) {
        this.repository = repository;
    }

    public List<ProveedorSearchEntity> findAllByStrings(List<String> conditions) {
        return repository.findAllByStrings(conditions);
    }

}
