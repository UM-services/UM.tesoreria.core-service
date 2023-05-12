/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;
import java.util.stream.DoubleStream;

import ar.edu.um.tesoreria.rest.kotlin.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

	public Optional<Usuario> findByPassword(String password);

	public Optional<Usuario> findByLogin(String login);

	public Optional<Usuario> findByUserId(Long userId);

}
