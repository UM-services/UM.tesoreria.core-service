/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import um.tesoreria.core.model.view.PersonaKey;

/**
 * @author daniel
 *
 */
public interface IPersonaKeyRepositoryCustom {

	public List<PersonaKey> findAllByStrings(List<String> conditions);

}
