/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Contratado;

/**
 * @author daniel
 *
 */
@Repository
public interface IContratadoRepository extends JpaRepository<Contratado, Long> {

	public Optional<Contratado> findByContratadoId(Long contratadoId);

	public Optional<Contratado> findByPersonaClave(Long personaClave);

	@Modifying
	public void deleteByContratadoId(Long contratadoId);

}
