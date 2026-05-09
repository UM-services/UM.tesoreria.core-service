/**
 * 
 */
package um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.entity.FacultadEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaFacultadRepository extends JpaRepository<FacultadEntity, Integer> {

	List<FacultadEntity> findAllByFacultadIdIn(List<Integer> facultadIds);
	Optional<FacultadEntity> findByFacultadId(Integer facultadId);

}
