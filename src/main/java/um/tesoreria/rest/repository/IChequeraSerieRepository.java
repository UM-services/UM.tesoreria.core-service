/**
 * 
 */
package um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import um.tesoreria.rest.kotlin.model.ChequeraSerie;

/**
 * @author daniel
 *
 */
public interface IChequeraSerieRepository extends JpaRepository<ChequeraSerie, Long> {

	public List<ChequeraSerie> findAllByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId, Sort sort);

	public List<ChequeraSerie> findAllByFacultadIdAndLectivoId(Integer facultadId, Integer lectivoId);

	public List<ChequeraSerie> findAllByFacultadIdAndChequeraSerieId(Integer facultadId, Long chequeraSerieId,
			Sort sort);

	public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndLectivoIdAndFacultadId(BigDecimal personaId,
			Integer documentoId, Integer lectivoId, Integer facultadId);

	public List<ChequeraSerie> findAllByLectivoIdAndFacultadId(Integer lectivoId, Integer facultadId);

	public List<ChequeraSerie> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId,
			Integer geograficaId);

	public List<ChequeraSerie> findAllByGeograficaIdAndFacultadIdInAndLectivoIdIn(Integer geograficaId,
			List<Integer> facultadIds, List<Integer> lectivoIds);

	public List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId,
			Integer tipoChequeraId);

	public List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndTipoChequeraIdIn(Integer facultadId, Integer lectivoId,
			List<Integer> tipoChequeraIds);

	public ChequeraSerie findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdInOrderByLectivoIdDesc(
			BigDecimal personaId, Integer documentoId, Integer facultadId, List<Integer> tipoChequeraIds);

	public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndFacultadId(BigDecimal personaId, Integer documentoId,
			Integer facultadId);

	public List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonaIdIn(Integer facultadId,
			Integer lectivoId, Integer geograficaId, List<BigDecimal> personaIds);

	public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndLectivoId(BigDecimal personaId, Integer documentoId, Integer lectivoId);

	public Optional<ChequeraSerie> findFirstByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoId(Integer facultadId,
			BigDecimal personaId, Integer documentoId, Integer lectivoId);

	public Optional<ChequeraSerie> findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
			Integer tipoChequeraId, Long chequeraSerieId);

	public Optional<ChequeraSerie> findByChequeraId(Long chequeraId);

	public Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
			BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId);

	public Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(
			BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId,
			Integer tipoChequeraId);

	public Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraIdIn(
			BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId,
			List<Integer> tipoChequeraIds);

	@Modifying
	public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}
