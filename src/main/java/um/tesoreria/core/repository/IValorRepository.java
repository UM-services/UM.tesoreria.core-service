/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Valor;

/**
 * @author daniel
 *
 */
@Repository
public interface IValorRepository extends JpaRepository<Valor, Integer> {

	public Optional<Valor> findByValorId(Integer valorId);

}
