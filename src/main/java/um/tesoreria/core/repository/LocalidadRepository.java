/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.Localidad;

/**
 * @author daniel
 *
 */
@Repository
public interface LocalidadRepository extends JpaRepository<Localidad, Long> {

	public List<Localidad> findAllByFacultadIdAndProvinciaId(Integer facultadId, Integer provinciaId);

	public Optional<Localidad> findByFacultadIdAndProvinciaIdAndNombre(Integer facultadId, Integer provinciaId,
			String nombre);

	public Optional<Localidad> findTopByFacultadIdAndProvinciaId(Integer facultadId, Integer provinciaId);

	public Optional<Localidad> findByFacultadIdAndProvinciaIdAndLocalidadId(Integer facultadId, Integer provinciaId,
			Integer localidadId);

}
