/**
 * 
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.UsuarioLdapException;
import um.tesoreria.core.model.UsuarioLdap;
import um.tesoreria.core.repository.IUsuarioLdapRepository;

/**
 * @author daniel
 *
 */
@Service
public class UsuarioLdapService {
	
	@Autowired
	private IUsuarioLdapRepository repository;

	public UsuarioLdap findByDocumento(BigDecimal documento) {
		return repository.findFirstByDocumento(documento).orElseThrow(() -> new UsuarioLdapException(documento));
	}

	public UsuarioLdap add(UsuarioLdap usuarioldap) {
		repository.save(usuarioldap);
		return usuarioldap;
	}

}
