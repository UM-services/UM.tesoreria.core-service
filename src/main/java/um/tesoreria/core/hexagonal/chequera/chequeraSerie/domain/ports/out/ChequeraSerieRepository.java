package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out;

import org.springframework.data.domain.Sort;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.model.internal.FacultadSedeChequeraDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ChequeraSerieRepository {

    List<ChequeraSerie> findAllByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId, Sort sort);

    List<ChequeraSerie> findAllByFacultadIdAndChequeraSerieId(Integer facultadId, Long chequeraSerieId, Sort sort);

    List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndLectivoIdAndFacultadId(BigDecimal personaId,
                                                                                        Integer documentoId, Integer lectivoId, Integer facultadId);

    List<ChequeraSerie> findAllByLectivoIdAndFacultadId(Integer lectivoId, Integer facultadId);

    List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndGeograficaId(Integer facultadId, Integer lectivoId, Integer geograficaId);

    List<ChequeraSerie> findAllByLectivoIdAndFacultadIdAndPersonaId(Integer lectivoId, Integer facultadId, BigDecimal personaId);

    List<ChequeraSerie> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId,
                                                                             Integer geograficaId);

    List<ChequeraSerie> findAllByGeograficaIdAndFacultadIdInAndLectivoIdIn(Integer geograficaId,
                                                                                List<Integer> facultadIds, List<Integer> lectivoIds);

    List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId,
                                                                              Integer tipoChequeraId);

    List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndFacultadId(BigDecimal personaId, Integer documentoId,
                                                                           Integer facultadId);

    List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId);

    List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonaIdIn(Integer facultadId,
                                                                                          Integer lectivoId, Integer geograficaId, List<BigDecimal> personaIds);

    List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndLectivoId(BigDecimal personaId, Integer documentoId, Integer lectivoId);

    Optional<ChequeraSerie> findFirstByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoId(Integer facultadId,
                                                                                             BigDecimal personaId, Integer documentoId, Integer lectivoId);

    Optional<ChequeraSerie> findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
                                                                                     Integer tipoChequeraId, Long chequeraSerieId);

    Optional<ChequeraSerie> findByChequeraId(Long chequeraId);

    Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId);

    Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId,
            Integer tipoChequeraId);

    Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraIdIn(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId,
            List<Integer> tipoChequeraIds);

    Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdIn(
            BigDecimal personaId, Integer documentoId, Integer facultadId, List<Integer> tipoChequeraIds, Sort sort);

    List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdIn(
            BigDecimal personaId, Integer documentoId, Integer facultadId, List<Integer> tipoChequeraIds, Sort sort);

    void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
                                                                       Long chequeraSerieId);

    List<FacultadSedeChequeraDto> findAllFacultadSedeByLectivo(Integer lectivoId);

    ChequeraSerie save(ChequeraSerie chequeraSerie);
}
