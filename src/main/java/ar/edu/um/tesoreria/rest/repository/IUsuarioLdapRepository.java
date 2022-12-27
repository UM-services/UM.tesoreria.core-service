/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.UsuarioLdap;

/**
 * @author daniel
 *
 */
@Repository
public interface IUsuarioLdapRepository extends JpaRepository<UsuarioLdap, Long> {

	public Optional<UsuarioLdap> findFirstByDocumento(BigDecimal documento);

}
