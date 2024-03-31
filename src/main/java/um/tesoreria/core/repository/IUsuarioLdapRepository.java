/**
 * 
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.UsuarioLdap;

/**
 * @author daniel
 *
 */
@Repository
public interface IUsuarioLdapRepository extends JpaRepository<UsuarioLdap, Long> {

	public Optional<UsuarioLdap> findFirstByDocumento(BigDecimal documento);

}
