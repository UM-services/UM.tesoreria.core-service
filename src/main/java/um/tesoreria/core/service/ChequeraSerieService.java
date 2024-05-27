/**
 *
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraSerieException;
import um.tesoreria.core.kotlin.model.ChequeraSerie;
import um.tesoreria.core.kotlin.model.TipoChequera;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAltaFull;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.model.dto.DeudaChequera;
import um.tesoreria.core.model.view.ChequeraIncompleta;
import um.tesoreria.core.model.view.ChequeraKey;
import um.tesoreria.core.repository.IChequeraSerieRepository;
import um.tesoreria.core.service.view.ChequeraSerieAltaFullService;
import um.tesoreria.core.service.view.ChequeraSerieAltaService;
import um.tesoreria.core.service.view.ChequeraIncompletaService;
import um.tesoreria.core.service.view.ChequeraKeyService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 */
@Service
@Slf4j
public class ChequeraSerieService {

    private final IChequeraSerieRepository repository;

    private final ChequeraIncompletaService chequeraIncompletaService;

    private final ChequeraSerieAltaService chequeraSerieAltaService;

    private final ChequeraSerieAltaFullService chequeraSerieAltaFullService;

    private final TipoChequeraService tipoChequeraService;

    private final DebitoService debitoService;

    private final ChequeraKeyService chequeraKeyService;

    @Autowired
    public ChequeraSerieService(IChequeraSerieRepository repository, ChequeraIncompletaService chequeraIncompletaService, ChequeraSerieAltaService chequeraSerieAltaService,
                                ChequeraSerieAltaFullService chequeraSerieAltaFullService, TipoChequeraService tipoChequeraService,
                                DebitoService debitoService, ChequeraKeyService chequeraKeyService) {
        this.repository = repository;
        this.chequeraIncompletaService = chequeraIncompletaService;
        this.chequeraSerieAltaService = chequeraSerieAltaService;
        this.chequeraSerieAltaFullService = chequeraSerieAltaFullService;
        this.tipoChequeraService = tipoChequeraService;
        this.debitoService = debitoService;
        this.chequeraKeyService = chequeraKeyService;

    }

    public List<ChequeraSerie> findAllByPersona(BigDecimal personaId, Integer documentoId) {
        return repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, Sort.by("lectivoId").descending());
    }

    public List<ChequeraSerie> findAllByPersonaExtended(BigDecimal personaId, Integer documentoId, ChequeraCuotaService chequeraCuotaService) {
        List<ChequeraSerie> chequeras = repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId,
                Sort.by("lectivoId").descending());
        for (ChequeraSerie chequera : chequeras) {
            DeudaChequera deuda = chequeraCuotaService.calculateDeuda(chequera.getFacultadId(),
                    chequera.getTipoChequeraId(), chequera.getChequeraSerieId());
            chequera.setCuotasDeuda(deuda.getCuotas());
            chequera.setImporteDeuda(deuda.getDeuda());
        }
        return chequeras;
    }

    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndLectivoIdAndFacultadId(BigDecimal personaId,
                                                                                         Integer documentoId, Integer lectivoId, Integer facultadId) {
        return repository.findAllByPersonaIdAndDocumentoIdAndLectivoIdAndFacultadId(personaId, documentoId, lectivoId,
                facultadId);
    }

    public List<ChequeraSerie> findAllByLectivoIdAndFacultadId(Integer lectivoId, Integer facultadId) {
        return repository.findAllByLectivoIdAndFacultadId(lectivoId, facultadId);
    }

    public List<ChequeraSerie> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId,
                                                                              Integer geograficaId) {
        return repository.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId);
    }

    public List<ChequeraSerie> findAllByGeograficaIdAndFacultadIdInAndLectivoIdIn(Integer geograficaId,
                                                                                  List<Integer> facultadIds, List<Integer> lectivoIds) {
        return repository.findAllByGeograficaIdAndFacultadIdInAndLectivoIdIn(geograficaId, facultadIds, lectivoIds);
    }

    public List<ChequeraSerie> findAllByNumber(Integer facultadId, Long chequeraSerieId) {
        return repository.findAllByFacultadIdAndChequeraSerieId(facultadId, chequeraSerieId,
                Sort.by("lectivoId").descending());
    }

    public List<ChequeraSerie> findAllByDocumentos(Integer facultadId, Integer lectivoId, Integer geograficaId,
                                                   List<BigDecimal> personaIds) {
        return repository.findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonaIdIn(facultadId, lectivoId,
                geograficaId, personaIds);
    }

    public List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId,
                                                                                Integer tipoChequeraId) {
        return repository.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId, lectivoId, tipoChequeraId);
    }

    public List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndTipoChequeraIdIn(Integer facultadId, Integer lectivoId,
                                                                                  List<Integer> tipoChequeraIds) {
        return repository.findAllByFacultadIdAndLectivoIdAndTipoChequeraIdIn(facultadId, lectivoId, tipoChequeraIds);
    }

    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId, Sort sort) {
        return repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, sort);
    }

    public List<ChequeraSerie> findAllByFacultad(BigDecimal personaId, Integer documentoId, Integer facultadId) {
        return repository.findAllByPersonaIdAndDocumentoIdAndFacultadId(personaId, documentoId, facultadId);
    }

    public List<ChequeraSerie> findAllByFacultadExtended(BigDecimal personaId, Integer documentoId,
                                                         Integer facultadId, ChequeraCuotaService chequeraCuotaService) {
        List<ChequeraSerie> chequeras = repository.findAllByPersonaIdAndDocumentoIdAndFacultadId(personaId, documentoId,
                facultadId);
        for (ChequeraSerie chequera : chequeras) {
            DeudaChequera deuda = chequeraCuotaService.calculateDeuda(chequera.getFacultadId(),
                    chequera.getTipoChequeraId(), chequera.getChequeraSerieId());
            chequera.setCuotasDeuda(deuda.getCuotas());
            chequera.setImporteDeuda(deuda.getDeuda());
        }
        return chequeras;
    }

    public List<ChequeraSerie> findAllByPersonaLectivo(BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        return repository.findAllByPersonaIdAndDocumentoIdAndLectivoId(personaId, documentoId, lectivoId);
    }

    public List<ChequeraIncompleta> findAllIncompletas(Integer lectivoId, Integer facultadId, Integer geograficaId) {
        return chequeraIncompletaService.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId,
                geograficaId);
    }

    public List<ChequeraSerieAlta> findAllAltas(Integer lectivoId, Integer facultadId, Integer geograficaId) {
        return chequeraSerieAltaService.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId);
    }

    public List<ChequeraSerieAltaFull> findAllAltasFull(Integer lectivoId, Integer facultadId, Integer geograficaId, Integer tipoChequeraId, OffsetDateTime fechaDesdePrimerVencimiento, ChequeraCuotaService chequeraCuotaService) {
        return chequeraSerieAltaFullService.findAllByLectivoIdAndFacultadIdAndGeograficaIdAndTipoChequeraId(lectivoId, facultadId, geograficaId, tipoChequeraId, fechaDesdePrimerVencimiento, chequeraCuotaService);
    }

    public List<ChequeraKey> findAllByCbu(String cbu, Integer debitoTipoId) {
        return chequeraKeyService.findAllByChequeraKey(debitoService.findAllByCbu(cbu, debitoTipoId).stream()
                .map(Debito::chequeraKey).collect(Collectors.toList()));
    }

    public List<ChequeraKey> findAllByVisa(String numeroTarjeta, Integer debitoTipoId) {
        return chequeraKeyService.findAllByChequeraKey(debitoService.findAllByVisa(numeroTarjeta, debitoTipoId).stream()
                .map(Debito::chequeraKey).collect(Collectors.toList()));
    }

    public ChequeraSerie findByChequeraId(Long chequeraId) {
        return repository.findByChequeraId(chequeraId).orElseThrow(() -> new ChequeraSerieException(chequeraId));
    }

    public ChequeraSerie findByChequeraIdExtended(Long chequeraId, ChequeraCuotaService chequeraCuotaService) {
        ChequeraSerie chequera = repository.findByChequeraId(chequeraId)
                .orElseThrow(() -> new ChequeraSerieException(chequeraId));
        DeudaChequera deuda = chequeraCuotaService.calculateDeuda(chequera.getFacultadId(),
                chequera.getTipoChequeraId(), chequera.getChequeraSerieId());
        chequera.setCuotasDeuda(deuda.getCuotas());
        chequera.setImporteDeuda(deuda.getDeuda());
        return chequera;
    }

    public ChequeraSerie findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
    }

    public ChequeraSerie findByUniqueExtended(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, ChequeraCuotaService chequeraCuotaService) {
        ChequeraSerie chequera = repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
        DeudaChequera deuda = chequeraCuotaService.calculateDeuda(chequera.getFacultadId(),
                chequera.getTipoChequeraId(), chequera.getChequeraSerieId());
        chequera.setCuotasDeuda(deuda.getCuotas());
        chequera.setImporteDeuda(deuda.getDeuda());
        return chequera;
    }

    public ChequeraSerie findByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId,
            Integer tipoChequeraId) {
        return repository
                .findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(personaId,
                        documentoId, facultadId, lectivoId, geograficaId, tipoChequeraId)
                .orElseThrow(() -> new ChequeraSerieException(personaId, documentoId, facultadId, lectivoId,
                        geograficaId, tipoChequeraId));
    }

    public ChequeraSerie findByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(BigDecimal personaId,
                                                                                               Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return repository
                .findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(personaId, documentoId,
                        facultadId, lectivoId, geograficaId)
                .orElseThrow(
                        () -> new ChequeraSerieException(personaId, documentoId, facultadId, lectivoId, geograficaId));
    }

    public ChequeraSerie findGradoByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return repository
                .findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraIdIn(
                        personaId, documentoId, facultadId, lectivoId, geograficaId,
                        tipoChequeraService.findAllByClaseChequera(2).stream().map(TipoChequera::getTipoChequeraId)
                                .collect(Collectors.toList()))
                .orElseThrow(
                        () -> new ChequeraSerieException(personaId, documentoId, facultadId, lectivoId, geograficaId));
    }

    public ChequeraSerie findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
            BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return repository
                .findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraIdIn(
                        personaId, documentoId, facultadId, lectivoId, geograficaId,
                        tipoChequeraService.findAllByClaseChequera(1).stream().map(TipoChequera::getTipoChequeraId)
                                .collect(Collectors.toList()))
                .orElseThrow(
                        () -> new ChequeraSerieException(personaId, documentoId, facultadId, lectivoId, geograficaId));
    }

    public ChequeraSerie findLastPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadId(BigDecimal personaId,
                                                                                        Integer documentoId, Integer facultadId) {
        return repository.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdInOrderByLectivoIdDesc(
                personaId, documentoId, facultadId, tipoChequeraService.findAllByClaseChequera(1).stream()
                        .map(TipoChequera::getTipoChequeraId).collect(Collectors.toList())).orElseThrow(() -> new ChequeraSerieException(personaId, documentoId, facultadId));
    }

    public ChequeraSerie setPayPerTic(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Byte flag) {
        ChequeraSerie chequeraSerie = repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
        chequeraSerie.setFlagPayperTic(flag);
        chequeraSerie = repository.save(chequeraSerie);
        return chequeraSerie;
    }

    public ChequeraSerie add(ChequeraSerie chequeraSerie) {
        chequeraSerie = repository.save(chequeraSerie);
        return chequeraSerie;
    }

    public ChequeraSerie update(ChequeraSerie newChequeraSerie, Long chequeraId) {
        Integer facultadId = newChequeraSerie.getFacultadId();
        Integer tipoChequeraId = newChequeraSerie.getTipoChequeraId();
        Long chequeraSerieId = newChequeraSerie.getChequeraSerieId();
        return repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .map(chequeraSerie -> {
                    chequeraSerie = new ChequeraSerie(chequeraId, facultadId, tipoChequeraId, chequeraSerieId,
                            newChequeraSerie.getPersonaId(), newChequeraSerie.getDocumentoId(),
                            newChequeraSerie.getLectivoId(), newChequeraSerie.getArancelTipoId(),
                            newChequeraSerie.getCursoId(), newChequeraSerie.getAsentado(),
                            newChequeraSerie.getGeograficaId(), newChequeraSerie.getFecha(),
                            newChequeraSerie.getCuotasPagadas(), newChequeraSerie.getObservaciones(),
                            newChequeraSerie.getAlternativaId(), newChequeraSerie.getAlgoPagado(),
                            newChequeraSerie.getTipoImpresionId(), newChequeraSerie.getFlagPayperTic(),
                            newChequeraSerie.getUsuarioId(), newChequeraSerie.getEnviado(),
                            newChequeraSerie.getRetenida(), newChequeraSerie.getVersion(), 0, BigDecimal.ZERO, null, null, null, null, null, null,
                            null);
                    chequeraSerie = repository.save(chequeraSerie);
                    log.debug("ChequeraSerie -> " + chequeraSerie);
                    return chequeraSerie;
                }).orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
    }

    @Transactional
    public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
                                                                         Long chequeraSerieId) {
        repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
                chequeraSerieId);
    }

}
