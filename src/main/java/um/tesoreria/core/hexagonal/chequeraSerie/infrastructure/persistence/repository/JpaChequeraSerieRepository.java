/**
 * 
 */
package um.tesoreria.core.hexagonal.chequeraSerie.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import um.tesoreria.core.hexagonal.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.model.internal.FacultadSedeChequeraDto;

/**
 * @author daniel
 *
 */
public interface JpaChequeraSerieRepository extends JpaRepository<ChequeraSerieEntity, Long> {

	List<ChequeraSerieEntity> findAllByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId, Sort sort);

	List<ChequeraSerieEntity> findAllByFacultadIdAndChequeraSerieId(Integer facultadId, Long chequeraSerieId,
	                                                                Sort sort);

	List<ChequeraSerieEntity> findAllByPersonaIdAndDocumentoIdAndLectivoIdAndFacultadId(BigDecimal personaId,
	                                                                                    Integer documentoId, Integer lectivoId, Integer facultadId);

	List<ChequeraSerieEntity> findAllByLectivoIdAndFacultadId(Integer lectivoId, Integer facultadId);

    List<ChequeraSerieEntity> findAllByFacultadIdAndLectivoIdAndGeograficaId(Integer facultadId, Integer lectivoId, Integer geograficaId);

    List<ChequeraSerieEntity> findAllByLectivoIdAndFacultadIdAndPersonaId(Integer lectivoId, Integer facultadId, BigDecimal bigDecimal);

    List<ChequeraSerieEntity> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId,
                                                                             Integer geograficaId);

	List<ChequeraSerieEntity> findAllByGeograficaIdAndFacultadIdInAndLectivoIdIn(Integer geograficaId,
	                                                                             List<Integer> facultadIds, List<Integer> lectivoIds);

	List<ChequeraSerieEntity> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId,
	                                                                           Integer tipoChequeraId);

	List<ChequeraSerieEntity> findAllByPersonaIdAndDocumentoIdAndFacultadId(BigDecimal personaId, Integer documentoId,
	                                                                        Integer facultadId);

	List<ChequeraSerieEntity> findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId);

	List<ChequeraSerieEntity> findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonaIdIn(Integer facultadId,
	                                                                                       Integer lectivoId, Integer geograficaId, List<BigDecimal> personaIds);

	List<ChequeraSerieEntity> findAllByPersonaIdAndDocumentoIdAndLectivoId(BigDecimal personaId, Integer documentoId, Integer lectivoId);

	Optional<ChequeraSerieEntity> findFirstByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoId(Integer facultadId,
	                                                                                          BigDecimal personaId, Integer documentoId, Integer lectivoId);

	Optional<ChequeraSerieEntity> findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
	                                                                                  Integer tipoChequeraId, Long chequeraSerieId);

	Optional<ChequeraSerieEntity> findByChequeraId(Long chequeraId);

	Optional<ChequeraSerieEntity> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
			BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId);

	Optional<ChequeraSerieEntity> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(
			BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId,
			Integer tipoChequeraId);

	Optional<ChequeraSerieEntity> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraIdIn(
			BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId,
			List<Integer> tipoChequeraIds);

	Optional<ChequeraSerieEntity> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdIn(
			BigDecimal personaId, Integer documentoId, Integer facultadId, List<Integer> tipoChequeraIds, Sort sort);

	@Modifying
	void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

    @Query("SELECT new um.tesoreria.core.model.internal.FacultadSedeChequeraDto(cs.facultadId, cs.facultad.nombre, cs.geograficaId, cs.geografica.nombre, COUNT(*)) " +
            "FROM ChequeraSerieEntity cs " +
            "WHERE cs.lectivoId = :lectivoId " +
            "GROUP BY cs.facultadId, cs.geograficaId " +
            "ORDER BY cs.facultadId, cs.geograficaId")
    List<FacultadSedeChequeraDto> findAllFacultadSedeByLectivo(@Param("lectivoId") Integer lectivoId);

}
