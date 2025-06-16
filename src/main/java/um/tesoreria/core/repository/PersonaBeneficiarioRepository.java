/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.PersonaBeneficiario;

/**
 * @author daniel
 *
 */
@Repository
public interface PersonaBeneficiarioRepository extends JpaRepository<PersonaBeneficiario, Long> {

	public Optional<PersonaBeneficiario> findByPersonaBeneficiarioId(Long personaBeneficiarioId);

	public Optional<PersonaBeneficiario> findByPersonaUniqueId(Long personaUniqueId);

	@Modifying
	public void deleteByPersonaUniqueId(Long personaUniqueId);

}
