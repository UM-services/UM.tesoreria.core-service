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

	public List<ArancelPorcentaje> findAllByAranceltipoId(Integer aranceltipoId);

	public Optional<ArancelPorcentaje> findByAranceltipoIdAndProductoId(Integer aranceltipoId, Integer productoId);

}
