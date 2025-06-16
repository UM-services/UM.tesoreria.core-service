/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.TipoChequeraLectivoSede;
import um.tesoreria.core.model.view.pk.TipoChequeraLectivoSedePk;

/**
 * @author daniel
 *
 */
@Repository
public interface TipoChequeraLectivoSedeRepository
		extends JpaRepository<TipoChequeraLectivoSede, TipoChequeraLectivoSedePk> {

	public List<TipoChequeraLectivoSede> findAllByFacultadIdAndLectivoId(Integer facultadId, Integer lectivoId);

	public List<TipoChequeraLectivoSede> findAllByFacultadIdAndLectivoIdAndGeograficaId(Integer facultadId,
			Integer lectivoId, Integer geograficaId);
}
