/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Curso;

/**
 * @author daniel
 *
 */
@Repository
public interface ICursoRepository extends JpaRepository<Curso, Integer> {

	public Optional<Curso> findTopByClaseChequeraId(Integer claseChequeraId);

}
