/**
 * 
 */
package um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.entity.GeograficaEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaGeograficaRepository extends JpaRepository<GeograficaEntity, Integer> {

	public List<GeograficaEntity> findAllByGeograficaId(Integer geograficaId);

	public Optional<GeograficaEntity> findByGeograficaId(Integer geograficaId);

}
