/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.FacultadLectivoSede;
import ar.edu.um.tesoreria.rest.model.view.pk.FacultadLectivoSedePk;

/**
 * @author daniel
 *
 */
@Repository
public interface IFacultadLectivoSedeRepository extends JpaRepository<FacultadLectivoSede, FacultadLectivoSedePk> {

	public List<FacultadLectivoSede> findAllByLectivoId(Integer lectivoId);

	public List<FacultadLectivoSede> findAllByLectivoIdAndGeograficaId(Integer lectivoId, Integer geograficaId);
}
