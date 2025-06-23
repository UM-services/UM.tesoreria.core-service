/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import um.tesoreria.core.kotlin.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByPassword(String password);

	Optional<Usuario> findByLogin(String login);

	Optional<Usuario> findByUserId(Long userId);

    Optional<Usuario> findByGoogleMailAndActivo(String googleMail, Byte activo);
}
