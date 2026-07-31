/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.InfoLdapException;
import um.tesoreria.core.model.InfoLdap;
import um.tesoreria.core.repository.InfoLdapRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class InfoLdapService {

	private final InfoLdapRepository repository;

	public InfoLdap findByPersonaId(BigDecimal personaId) {
		return repository.findByPersonaId(personaId).orElseThrow(() -> new InfoLdapException(personaId));
	}

}
