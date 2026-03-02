/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import um.tesoreria.core.kotlin.model.ArancelTipo;

/**
 * @author daniel
 *
 */
public interface ArancelTipoRepository extends JpaRepository<ArancelTipo, Integer> {

	List<ArancelTipo> findAllByHabilitado(Byte habilitado, Sort sort);

	Optional<ArancelTipo> findByArancelTipoId(Integer arancelTipoId);

	Optional<ArancelTipo> findTopByOrderByArancelTipoIdDesc();

	Optional<ArancelTipo> findByArancelTipoIdCompleto(Integer arancelTipoIdCompleto);

	@Modifying
	void deleteByArancelTipoId(Integer arancelTipoId);

}
