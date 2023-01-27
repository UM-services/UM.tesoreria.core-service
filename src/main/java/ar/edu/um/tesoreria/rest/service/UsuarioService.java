/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.UsuarioException;
import ar.edu.um.tesoreria.rest.model.Usuario;
import ar.edu.um.tesoreria.rest.repository.IUsuarioRepository;
import ar.edu.um.tesoreria.rest.util.Tool;

/**
 * @author daniel
 *
 */
@Service
public class UsuarioService {

	@Autowired
	private IUsuarioRepository repository;

	public Usuario findByUsuarioId(String usuarioId) {
		return repository.findByUsuarioId(usuarioId).orElseThrow(() -> new UsuarioException(usuarioId));
	}

	public Usuario findByPassword(String password) {
		return repository.findByPassword(DigestUtils.sha256Hex(password))
				.orElseThrow(() -> new UsuarioException());
	}

	public Usuario add(Usuario usuario) {
		usuario.setPassword(DigestUtils.sha256Hex(usuario.getPassword()));
		usuario = repository.save(usuario);
		return usuario;
	}

	public Usuario update(Usuario newUsuario, String usuarioId) {
		return repository.findByUsuarioId(usuarioId).map(usuario -> {
			usuario = new Usuario(usuarioId, DigestUtils.sha256Hex(newUsuario.getPassword()), newUsuario.getNombre(),
					newUsuario.getGeograficaId(), newUsuario.getImprimeChequera(), newUsuario.getNumeroOpManual(),
					newUsuario.getHabilitaOpEliminacion(), newUsuario.getEliminaChequera(),
					Tool.hourAbsoluteArgentina(), newUsuario.getAutoId());
			usuario = repository.save(usuario);
			return usuario;
		}).orElseThrow(() -> new UsuarioException(usuarioId));
	}

	public Usuario updateLastLog(String usuarioId) {
		return repository.findByUsuarioId(usuarioId).map(usuario -> {
			usuario = new Usuario(usuarioId, usuario.getPassword(), usuario.getNombre(), usuario.getGeograficaId(),
					usuario.getImprimeChequera(), usuario.getNumeroOpManual(), usuario.getHabilitaOpEliminacion(),
					usuario.getEliminaChequera(), Tool.hourAbsoluteArgentina(), usuario.getAutoId());
			usuario = repository.save(usuario);
			return usuario;
		}).orElseThrow(() -> new UsuarioException(usuarioId));
	}

}
