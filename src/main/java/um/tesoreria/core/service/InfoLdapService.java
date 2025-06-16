/**
 * 
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.InfoLdapException;
import um.tesoreria.core.model.InfoLdap;
import um.tesoreria.core.repository.InfoLdapRepository;

/**
 * @author daniel
 *
 */
@Service
public class InfoLdapService {

	@Autowired
	private InfoLdapRepository repository;

	public InfoLdap findByPersonaId(BigDecimal personaId) {
		return repository.findByPersonaId(personaId).orElseThrow(() -> new InfoLdapException(personaId));
	}

}
