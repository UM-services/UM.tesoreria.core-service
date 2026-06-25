/**
 * 
 */
package um.tesoreria.core.hexagonal.asiento.infrastructure.persistence.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.asiento.infrastructure.persistence.entity.AsientoEntity;


/**
 * @author daniel
 *
 */
@Repository
public interface JpaAsientoRepository extends JpaRepository<AsientoEntity, Long> {

	Optional<AsientoEntity> findByFechaAndOrden(OffsetDateTime fecha, Integer orden);

	Optional<AsientoEntity> findByAsientoId(Long asientoId);

	@Modifying
	void deleteByAsientoId(Long asientoId);

}
