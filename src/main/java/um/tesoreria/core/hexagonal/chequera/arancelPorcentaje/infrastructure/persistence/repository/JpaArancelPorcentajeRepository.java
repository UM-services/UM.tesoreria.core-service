/**
 * 
 */
package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.persistence.entity.ArancelPorcentajeEntity;

/**
 * @author daniel
 *
 */
public interface JpaArancelPorcentajeRepository extends JpaRepository<ArancelPorcentajeEntity, Long> {

	List<ArancelPorcentajeEntity> findAllByAranceltipoId(Integer aranceltipoId);

	Optional<ArancelPorcentajeEntity> findByAranceltipoIdAndProductoId(Integer aranceltipoId, Integer productoId);

}
