/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.UsuarioLdapNotFoundException;
import ar.edu.um.tesoreria.rest.model.UsuarioLdap;
import ar.edu.um.tesoreria.rest.repository.IUsuarioLdapRepository;

/**
 * @author daniel
 *
 */
@Service
public class UsuarioLdapService {
	
	@Autowired
	private IUsuarioLdapRepository repository;

	public UsuarioLdap findByDocumento(BigDecimal documento) {
		return repository.findFirstByDocumento(documento).orElseThrow(() -> new UsuarioLdapNotFoundException(documento));
	}

	public UsuarioLdap add(UsuarioLdap usuarioldap) {
		repository.save(usuarioldap);
		return usuarioldap;
	}

}
