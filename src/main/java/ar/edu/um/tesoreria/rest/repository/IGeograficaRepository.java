/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.Geografica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IGeograficaRepository extends JpaRepository<Geografica, Integer> {

	public List<Geografica> findAllByGeograficaId(Integer geograficaId);

	public Optional<Geografica> findByGeograficaId(Integer geograficaId);

}
