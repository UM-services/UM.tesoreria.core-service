/**
 * 
 */
package um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.entity.ArticuloEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaArticuloRepository extends JpaRepository<ArticuloEntity, Long> {

	public Optional<ArticuloEntity> findByArticuloId(Long articuloId);

	public Optional<ArticuloEntity> findTopByOrderByArticuloIdDesc();

}
