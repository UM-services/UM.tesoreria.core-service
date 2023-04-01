/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import ar.edu.um.tesoreria.rest.kotlin.model.ArancelTipo;

/**
 * @author daniel
 *
 */
public interface IArancelTipoRepository extends JpaRepository<ArancelTipo, Integer> {

	public Optional<ArancelTipo> findByArancelTipoId(Integer arancelTipoId);

	public Optional<ArancelTipo> findTopByOrderByArancelTipoIdDesc();

	public Optional<ArancelTipo> findByArancelTipoIdCompleto(Integer arancelTipoIdCompleto);

	@Modifying
	public void deleteByArancelTipoId(Integer arancelTipoId);

}
