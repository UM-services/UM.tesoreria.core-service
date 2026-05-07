/**
 * 
 */
package um.tesoreria.core.hexagonal.ubicacion.infrastructure.persistence.repository;

import java.util.List;

import um.tesoreria.core.hexagonal.ubicacion.infrastructure.persistence.entity.UbicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaUbicacionRepository extends JpaRepository<UbicacionEntity, Integer> {

	List<UbicacionEntity> findAllByDependenciaIdNotNull();

}
