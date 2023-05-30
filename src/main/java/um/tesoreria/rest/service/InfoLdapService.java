/**
 * 
 */
package um.tesoreria.rest.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.InfoLdapException;
import um.tesoreria.rest.model.InfoLdap;
import um.tesoreria.rest.repository.IInfoLdapRepository;
import um.tesoreria.rest.exception.InfoLdapException;
import um.tesoreria.rest.repository.IInfoLdapRepository;

/**
 * @author daniel
 *
 */
@Service
public class InfoLdapService {

	@Autowired
	private IInfoLdapRepository repository;

	public InfoLdap findByPersonaId(BigDecimal personaId) {
		return repository.findByPersonaId(personaId).orElseThrow(() -> new InfoLdapException(personaId));
	}

}
