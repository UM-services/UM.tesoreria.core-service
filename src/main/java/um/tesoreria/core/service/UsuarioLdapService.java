/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.UsuarioLdapException;
import um.tesoreria.core.model.UsuarioLdap;
import um.tesoreria.core.repository.UsuarioLdapRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class UsuarioLdapService {
	
	private final UsuarioLdapRepository repository;

	public UsuarioLdap findByDocumento(BigDecimal documento) {
		return repository.findFirstByDocumento(documento).orElseThrow(() -> new UsuarioLdapException(documento));
	}

	public UsuarioLdap add(UsuarioLdap usuarioldap) {
		repository.save(usuarioldap);
		return usuarioldap;
	}

}
