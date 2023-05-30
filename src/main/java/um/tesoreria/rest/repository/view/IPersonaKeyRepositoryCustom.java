/**
 * 
 */
package um.tesoreria.rest.repository.view;

import java.util.List;

import um.tesoreria.rest.model.view.PersonaKey;

/**
 * @author daniel
 *
 */
public interface IPersonaKeyRepositoryCustom {

	public List<PersonaKey> findAllByStrings(List<String> conditions);

}
