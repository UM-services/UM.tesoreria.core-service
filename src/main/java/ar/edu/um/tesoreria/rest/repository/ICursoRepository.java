/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface ICursoRepository extends JpaRepository<Curso, Integer> {

	public Optional<Curso> findTopByClaseChequeraId(Integer claseChequeraId);

}
