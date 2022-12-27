/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Usuario;

/**
 * @author daniel
 *
 */
@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, String> {

	public Optional<Usuario> findByPassword(String password);

	public Optional<Usuario> findByUsuarioId(String usuarioId);

}
