/**
 * 
 */
package um.tesoreria.core.hexagonal.dependencia.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.dependencia.infrastructure.persistence.entity.DependenciaEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaDependenciaRepository extends JpaRepository<DependenciaEntity, Integer> {

    List<DependenciaEntity> findAllByOrderByNombre();

    Optional<DependenciaEntity> findByDependenciaId(Integer dependenciaId);

}
