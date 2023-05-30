/**
 * 
 */
package um.tesoreria.rest.repository.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.view.ChequeraClase;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraClaseRepository extends JpaRepository<ChequeraClase, Long> {

	public Optional<ChequeraClase> findFirstByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClaseChequeraIdIn(
			Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId, List<Integer> clases);

	public Optional<ChequeraClase> findTopByFacultadIdAndPersonaIdAndDocumentoIdAndClaseChequeraIdAndLectivoIdLessThanEqualOrderByLectivoIdDesc(
			Integer facultadId, BigDecimal personaId, Integer documentoId, Integer claseChequeraId, Integer lectivoId);

}
