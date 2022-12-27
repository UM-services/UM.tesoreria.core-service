/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.InfoLdap;

/**
 * @author daniel
 *
 */
@Repository
public interface IInfoLdapRepository extends JpaRepository<InfoLdap, BigDecimal> {

	public Optional<InfoLdap> findByPersonaId(BigDecimal personaId);

}
