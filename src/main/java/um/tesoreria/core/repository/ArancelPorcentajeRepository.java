/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import um.tesoreria.core.kotlin.model.ArancelPorcentaje;

/**
 * @author daniel
 *
 */
public interface ArancelPorcentajeRepository extends JpaRepository<ArancelPorcentaje, Long> {

	List<ArancelPorcentaje> findAllByAranceltipoId(Integer aranceltipoId);

	Optional<ArancelPorcentaje> findByAranceltipoIdAndProductoId(Integer aranceltipoId, Integer productoId);

}
