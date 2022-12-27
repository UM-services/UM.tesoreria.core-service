/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.TipoChequeraLectivoSede;
import ar.edu.um.tesoreria.rest.model.view.pk.TipoChequeraLectivoSedePk;

/**
 * @author daniel
 *
 */
@Repository
public interface ITipoChequeraLectivoSedeRepository
		extends JpaRepository<TipoChequeraLectivoSede, TipoChequeraLectivoSedePk> {

	public List<TipoChequeraLectivoSede> findAllByFacultadIdAndLectivoId(Integer facultadId, Integer lectivoId);

	public List<TipoChequeraLectivoSede> findAllByFacultadIdAndLectivoIdAndGeograficaId(Integer facultadId,
			Integer lectivoId, Integer geograficaId);
}
