package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.*;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAltaFull;
import um.tesoreria.core.model.internal.FacultadSedeChequeraDto;
import um.tesoreria.core.model.view.ChequeraIncompleta;
import um.tesoreria.core.model.view.ChequeraKey;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChequeraSerieService {

    private final CreateChequeraSerieUseCase createChequeraSerieUseCase;
    private final GetChequeraSerieByIdUseCase getChequeraSerieByIdUseCase;
    private final GetChequeraSerieByUniqueUseCase getChequeraSerieByUniqueUseCase;
    private final GetChequeraSerieByPersonaUseCase getChequeraSerieByPersonaUseCase;
    private final GetChequeraSerieByFacultadUseCase getChequeraSerieByFacultadUseCase;
    private final GetChequeraSerieByNumberUseCase getChequeraSerieByNumberUseCase;
    private final GetChequeraSerieIncompletasUseCase getChequeraSerieIncompletasUseCase;
    private final GetChequeraSerieAltasUseCase getChequeraSerieAltasUseCase;
    private final GetChequeraSerieByCbuOrVisaUseCase getChequeraSerieByCbuOrVisaUseCase;
    private final GetChequeraSerieSpecialQueriesUseCase getChequeraSerieSpecialQueriesUseCase;
    private final SetPayPerTicUseCase setPayPerTicUseCase;
    private final MarkSentUseCase markSentUseCase;
    private final UpdateChequeraSerieUseCase updateChequeraSerieUseCase;
    private final DeleteChequeraSerieUseCase deleteChequeraSerieUseCase;
    private final GetResumenLectivoUseCase getResumenLectivoUseCase;

    public List<ChequeraSerie> findAllByPersona(BigDecimal personaId, Integer documentoId) {
        return getChequeraSerieByPersonaUseCase.getAllByPersona(personaId, documentoId);
    }

    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId) {
        log.debug("\n\nProcessing ChequeraSerieService.findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId\n\n");
        return getChequeraSerieByPersonaUseCase.getAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(personaId, documentoId, facultadId, lectivoId);
    }

    public List<ChequeraSerie> findAllByPersonaExtended(BigDecimal personaId, Integer documentoId) {
        return getChequeraSerieByPersonaUseCase.getAllByPersonaExtended(personaId, documentoId);
    }

    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndLectivoIdAndFacultadId(BigDecimal personaId,
                                                                                               Integer documentoId, Integer lectivoId, Integer facultadId) {
        return getChequeraSerieByPersonaUseCase.getAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(personaId, documentoId, facultadId, lectivoId);
    }

    public List<ChequeraSerie> findAllByLectivoIdAndFacultadId(Integer lectivoId, Integer facultadId) {
        return getChequeraSerieByFacultadUseCase.getAllByLectivoAndFacultad(lectivoId, facultadId);
    }

    public List<ChequeraSerie> findAllBySede(Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return getChequeraSerieByFacultadUseCase.getAllBySede(facultadId, lectivoId, geograficaId);
    }

    public List<ChequeraSerie> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId,
                                                                                    Integer geograficaId) {
        return getChequeraSerieByFacultadUseCase.getAllByLectivoAndFacultadAndGeografica(lectivoId, facultadId, geograficaId);
    }

    public List<ChequeraSerie> findAllByGeograficaIdAndFacultadIdInAndLectivoIdIn(Integer geograficaId,
                                                                                        List<Integer> facultadIds, List<Integer> lectivoIds) {
        return getChequeraSerieByFacultadUseCase.getAllByGeograficaAndFacultadsAndLectivos(geograficaId, facultadIds, lectivoIds);
    }

    public List<ChequeraSerie> findAllByNumber(Integer facultadId, Long chequeraSerieId) {
        return getChequeraSerieByNumberUseCase.getAllByNumber(facultadId, chequeraSerieId);
    }

    public List<ChequeraSerie> findAllByDocumentos(Integer facultadId, Integer lectivoId, Integer geograficaId,
                                                         List<BigDecimal> personaIds) {
        return getChequeraSerieByFacultadUseCase.getAllByDocumentos(facultadId, lectivoId, geograficaId, personaIds);
    }

    public List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId,
                                                                                      Integer tipoChequeraId) {
        return getChequeraSerieByFacultadUseCase.getAllByFacultadAndLectivoAndTipo(facultadId, lectivoId, tipoChequeraId);
    }

    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId, Sort sort) {
        return getChequeraSerieByPersonaUseCase.getAllByPersonaIdAndDocumentoId(personaId, documentoId, sort);
    }

    public List<ChequeraSerie> findAllByFacultad(BigDecimal personaId, Integer documentoId, Integer facultadId) {
        return getChequeraSerieByFacultadUseCase.getAllByFacultad(personaId, documentoId, facultadId);
    }

    public List<ChequeraSerie> findAllByFacultadIdAndPersonaIdAndDocumentoId(Integer facultadId, BigDecimal personaId, Integer documentoId) {
        return getChequeraSerieByFacultadUseCase.getAllByFacultad(personaId, documentoId, facultadId);
    }

    public List<ChequeraSerie> findAllByFacultadExtended(BigDecimal personaId, Integer documentoId,
                                                               Integer facultadId) {
        return getChequeraSerieByFacultadUseCase.getAllByFacultadExtended(personaId, documentoId, facultadId);
    }

    public List<ChequeraSerie> findAllByPersonaLectivo(BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        return getChequeraSerieByPersonaUseCase.getAllByPersonaLectivo(personaId, documentoId, lectivoId);
    }

    public List<ChequeraIncompleta> findAllIncompletas(Integer lectivoId, Integer facultadId, Integer geograficaId) {
        return getChequeraSerieIncompletasUseCase.getAllIncompletas(lectivoId, facultadId, geograficaId);
    }

    public List<ChequeraSerieAlta> findAllAltas(Integer lectivoId, Integer facultadId, Integer geograficaId) {
        return getChequeraSerieAltasUseCase.getAllAltas(lectivoId, facultadId, geograficaId);
    }

    public List<ChequeraSerieAltaFull> findAllAltasFull(Integer lectivoId, Integer facultadId, Integer geograficaId, Integer tipoChequeraId, OffsetDateTime fechaDesdePrimerVencimiento, ChequeraCuotaService chequeraCuotaService) {
        return getChequeraSerieAltasUseCase.getAllAltasFull(lectivoId, facultadId, geograficaId, tipoChequeraId, fechaDesdePrimerVencimiento, chequeraCuotaService);
    }

    public List<ChequeraKey> findAllByCbu(String cbu, Integer debitoTipoId) {
        return getChequeraSerieByCbuOrVisaUseCase.getAllByCbu(cbu, debitoTipoId);
    }

    public List<ChequeraKey> findAllByVisa(String numeroTarjeta, Integer debitoTipoId) {
        return getChequeraSerieByCbuOrVisaUseCase.getAllByVisa(numeroTarjeta, debitoTipoId);
    }

    public ChequeraSerie findByChequeraId(Long chequeraId) {
        return getChequeraSerieByIdUseCase.getById(chequeraId).orElseThrow(() -> new ChequeraSerieException(chequeraId));
    }

    public ChequeraSerie findByChequeraIdExtended(Long chequeraId) {
        return getChequeraSerieByIdUseCase.getByIdExtended(chequeraId).orElseThrow(() -> new ChequeraSerieException(chequeraId));
    }

    public ChequeraSerie findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        log.debug("\n\nProcessing ChequeraSerieService.findByUnique\n\n");
        return getChequeraSerieByUniqueUseCase.getByUnique(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
    }

    public ChequeraSerie findByUniqueExtended(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return getChequeraSerieByUniqueUseCase.getByUniqueExtended(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
    }

    public ChequeraSerie findByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId,
            Integer tipoChequeraId) {
        return getChequeraSerieSpecialQueriesUseCase
                .findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(personaId,
                        documentoId, facultadId, lectivoId, geograficaId, tipoChequeraId)
                .orElseThrow(() -> new ChequeraSerieException(personaId, documentoId, facultadId, lectivoId,
                        geograficaId, tipoChequeraId));
    }

    public ChequeraSerie findByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(BigDecimal personaId,
                                                                                                     Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return getChequeraSerieSpecialQueriesUseCase
                .findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(personaId, documentoId,
                        facultadId, lectivoId, geograficaId)
                .orElseThrow(
                        () -> new ChequeraSerieException(personaId, documentoId, facultadId, lectivoId, geograficaId));
    }

    public ChequeraSerie findGradoByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return getChequeraSerieSpecialQueriesUseCase
                .findGradoByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                        personaId, documentoId, facultadId, lectivoId, geograficaId)
                .orElseThrow(
                        () -> new ChequeraSerieException(personaId, documentoId, facultadId, lectivoId, geograficaId));
    }

    public ChequeraSerie findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return getChequeraSerieSpecialQueriesUseCase
                .findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
                        personaId, documentoId, facultadId, lectivoId, geograficaId)
                .orElseThrow(
                        () -> new ChequeraSerieException(personaId, documentoId, facultadId, lectivoId, geograficaId));
    }

    public ChequeraSerie findLastPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadId(BigDecimal personaId,
                                                                                              Integer documentoId, Integer facultadId) {
        return getChequeraSerieSpecialQueriesUseCase
                .findLastPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadId(personaId, documentoId, facultadId)
                .orElseThrow(() -> new ChequeraSerieException(personaId, documentoId, facultadId));
    }

    public ChequeraSerie setPayPerTic(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Byte flag) {
        return setPayPerTicUseCase.setPayPerTic(facultadId, tipoChequeraId, chequeraSerieId, flag);
    }

    public ChequeraSerie markSent(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        log.debug("Processing ChequeraSerieService.markSent");
        return markSentUseCase.markSent(facultadId, tipoChequeraId, chequeraSerieId);
    }

    public ChequeraSerie add(ChequeraSerie chequeraSerie) {
        log.debug("Processing ChequeraSerieService.add");
        return createChequeraSerieUseCase.create(chequeraSerie);
    }

    public ChequeraSerie update(ChequeraSerie newChequeraSerie, Long chequeraId) {
        log.debug("Processing ChequeraSerieService.update for id {}", chequeraId);
        return updateChequeraSerieUseCase.update(newChequeraSerie, chequeraId);
    }

    public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
                                                                         Long chequeraSerieId) {
        deleteChequeraSerieUseCase.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
                chequeraSerieId);
    }

    public List<FacultadSedeChequeraDto> resumenLectivo(Integer lectivoId) {
        return getResumenLectivoUseCase.resumenLectivo(lectivoId);
    }

}
