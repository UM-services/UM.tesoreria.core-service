/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.um.tesoreria.rest.model.ArancelPorcentaje;

/**
 * @author daniel
 *
 */
public interface IArancelPorcentajeRepository extends JpaRepository<ArancelPorcentaje, Long> {

	public List<ArancelPorcentaje> findAllByAranceltipoId(Integer aranceltipoId);

	public Optional<ArancelPorcentaje> findByAranceltipoIdAndProductoId(Integer aranceltipoId, Integer productoId);

}
