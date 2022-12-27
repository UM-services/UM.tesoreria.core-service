/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Curso;

/**
 * @author daniel
 *
 */
@Repository
public interface ICursoRepository extends JpaRepository<Curso, Integer> {

	public Optional<Curso> findTopByClaseChequeraId(Integer claseChequeraId);

}
