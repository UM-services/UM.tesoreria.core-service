/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Articulo;

/**
 * @author daniel
 *
 */
@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Long> {

	public Optional<Articulo> findByArticuloId(Long articuloId);

	public Optional<Articulo> findTopByOrderByArticuloIdDesc();

}
