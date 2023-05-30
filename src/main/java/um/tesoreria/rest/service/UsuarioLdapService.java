/**
 * 
 */
package um.tesoreria.rest.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.UsuarioLdapException;
import um.tesoreria.rest.model.UsuarioLdap;
import um.tesoreria.rest.repository.IUsuarioLdapRepository;
import um.tesoreria.rest.exception.UsuarioLdapException;
import um.tesoreria.rest.repository.IUsuarioLdapRepository;

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
