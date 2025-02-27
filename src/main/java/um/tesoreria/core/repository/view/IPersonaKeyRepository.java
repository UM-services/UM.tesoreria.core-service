/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.PersonaKey;

/**
 * @author daniel
 *
 */
@Repository
public interface IPersonaKeyRepository extends JpaRepository<PersonaKey, String>, IPersonaKeyRepositoryCustom {

	public List<PersonaKey> findAllByUnifiedIn(List<String> keys, Sort sort);

}
