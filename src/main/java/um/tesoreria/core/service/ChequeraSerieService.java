/**
 *
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
import um.tesoreria.core.repository.ChequeraSerieRepository;
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

    private final ChequeraSerieRepository repository;
    private final ChequeraIncompletaService chequeraIncompletaService;
    private final ChequeraSerieAltaService chequeraSerieAltaService;
    private final ChequeraSerieAltaFullService chequeraSerieAltaFullService;
    private final TipoChequeraService tipoChequeraService;
    private final DebitoService debitoService;
    private final ChequeraKeyService chequeraKeyService;
    private final ChequeraImpresionCabeceraService chequeraImpresionCabeceraService;

    @Autowired
    public ChequeraSerieService(ChequeraSerieRepository repository, ChequeraIncompletaService chequeraIncompletaService, ChequeraSerieAltaService chequeraSerieAltaService,
                                ChequeraSerieAltaFullService chequeraSerieAltaFullService, TipoChequeraService tipoChequeraService,
                                DebitoService debitoService, ChequeraKeyService chequeraKeyService, ChequeraImpresionCabeceraService chequeraImpresionCabeceraService) {
        this.repository = repository;
        this.chequeraIncompletaService = chequeraIncompletaService;
        this.chequeraSerieAltaService = chequeraSerieAltaService;
        this.chequeraSerieAltaFullService = chequeraSerieAltaFullService;
        this.tipoChequeraService = tipoChequeraService;
        this.debitoService = debitoService;
        this.chequeraKeyService = chequeraKeyService;
        this.chequeraImpresionCabeceraService = chequeraImpresionCabeceraService;
    }

    public List<ChequeraSerie> findAllByPersona(BigDecimal personaId, Integer documentoId) {
        return repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, Sort.by("lectivoId").descending());
    }

    public List<ChequeraSerie> findAllByPersonaExtended(BigDecimal personaId, Integer documentoId, ChequeraCuotaService chequeraCuotaService) {
        return repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, Sort.by("lectivoId").descending())
            .stream()
            .peek(chequera -> {
                var deuda = chequeraCuotaService.calculateDeuda(
                    chequera.getFacultadId(),
                    chequera.getTipoChequeraId(), 
                    chequera.getChequeraSerieId()
                );
                chequera.setCuotasDeuda(deuda.getCuotas());
                chequera.setImporteDeuda(deuda.getDeuda());
            })
            .toList();
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

    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId, Sort sort) {
        return repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, sort);
    }

    public List<ChequeraSerie> findAllByFacultad(BigDecimal personaId, Integer documentoId, Integer facultadId) {
        return repository.findAllByPersonaIdAndDocumentoIdAndFacultadId(personaId, documentoId, facultadId);
    }

    public List<ChequeraSerie> findAllByFacultadExtended(BigDecimal personaId, Integer documentoId,
                                                        Integer facultadId, ChequeraCuotaService chequeraCuotaService) {
        return repository.findAllByPersonaIdAndDocumentoIdAndFacultadId(personaId, documentoId, facultadId)
            .stream()
            .peek(chequera -> {
                var deuda = chequeraCuotaService.calculateDeuda(
                    chequera.getFacultadId(),
                    chequera.getTipoChequeraId(), 
                    chequera.getChequeraSerieId()
                );
                chequera.setCuotasDeuda(deuda.getCuotas());
                chequera.setImporteDeuda(deuda.getDeuda());
            })
            .toList();
    }

    public List<ChequeraSerie> findAllByPersonaLectivo(BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        var chequeras = repository.findAllByPersonaIdAndDocumentoIdAndLectivoId(personaId, documentoId, lectivoId);
        return chequeras.stream().peek(chequera ->
                chequera.setUltimoEnvio(Objects.requireNonNull(chequeraImpresionCabeceraService.findLastByUnique(chequera.getFacultadId(),
                chequera.getTipoChequeraId(), chequera.getChequeraSerieId()).getFecha()).plusHours(-3))).toList();
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
        var chequeraSerie = repository.findByChequeraId(chequeraId).orElseThrow(() -> new ChequeraSerieException(chequeraId));
        logChequeraSerie(chequeraSerie);
        return chequeraSerie;
    }

    public ChequeraSerie findByChequeraIdExtended(Long chequeraId, ChequeraCuotaService chequeraCuotaService) {
        ChequeraSerie chequera = repository.findByChequeraId(chequeraId)
                .orElseThrow(() -> new ChequeraSerieException(chequeraId));
        var deuda = chequeraCuotaService.calculateDeuda(chequera.getFacultadId(),
                chequera.getTipoChequeraId(), chequera.getChequeraSerieId());
        chequera.setCuotasDeuda(deuda.getCuotas());
        chequera.setImporteDeuda(deuda.getDeuda());
        return chequera;
    }

    public ChequeraSerie findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        var chequera = repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
        chequera.setUltimoEnvio(Objects.requireNonNull(chequeraImpresionCabeceraService.findLastByUnique(facultadId, tipoChequeraId, chequeraSerieId).getFecha()).plusHours(-3));
        logChequeraSerie(chequera);
        return chequera;
    }

    public ChequeraSerie findByUniqueExtended(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, ChequeraCuotaService chequeraCuotaService) {
        ChequeraSerie chequera = repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
        var deuda = chequeraCuotaService.calculateDeuda(chequera.getFacultadId(),
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
        var chequeraSerie = repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
        chequeraSerie.setFlagPayperTic(flag);
        chequeraSerie = repository.save(chequeraSerie);
        return chequeraSerie;
    }

    public ChequeraSerie add(ChequeraSerie chequeraSerie) {
        log.debug("Processing ChequeraSerieService.add");
        chequeraSerie = repository.save(chequeraSerie);
        logChequeraSerie(chequeraSerie);
        return chequeraSerie;
    }

    public ChequeraSerie update(ChequeraSerie newChequeraSerie, Long chequeraId) {
        log.debug("Processing ChequeraSerieService.update");
        Integer facultadId = newChequeraSerie.getFacultadId();
        Integer tipoChequeraId = newChequeraSerie.getTipoChequeraId();
        Long chequeraSerieId = newChequeraSerie.getChequeraSerieId();
        return repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .map(chequeraSerie -> {
                    chequeraSerie = new ChequeraSerie(chequeraId,
                            facultadId,
                            tipoChequeraId,
                            chequeraSerieId,
                            newChequeraSerie.getPersonaId(),
                            newChequeraSerie.getDocumentoId(),
                            newChequeraSerie.getLectivoId(),
                            newChequeraSerie.getArancelTipoId(),
                            newChequeraSerie.getCursoId(),
                            newChequeraSerie.getAsentado(),
                            newChequeraSerie.getGeograficaId(),
                            newChequeraSerie.getFecha(),
                            newChequeraSerie.getCuotasPagadas(),
                            newChequeraSerie.getObservaciones(),
                            newChequeraSerie.getAlternativaId(),
                            newChequeraSerie.getAlgoPagado(),
                            newChequeraSerie.getTipoImpresionId(),
                            newChequeraSerie.getFlagPayperTic(),
                            newChequeraSerie.getUsuarioId(),
                            newChequeraSerie.getEnviado(),
                            newChequeraSerie.getRetenida(),
                            newChequeraSerie.getVersion(),
                            0,
                            BigDecimal.ZERO,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                    );
                    chequeraSerie = repository.save(chequeraSerie);
                    logChequeraSerie(chequeraSerie);
                    return chequeraSerie;
                }).orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
    }

    @Transactional
    public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
                                                                         Long chequeraSerieId) {
        repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
                chequeraSerieId);
    }

    private void logChequeraSerie(ChequeraSerie chequeraSerie) {
        try {
            log.debug("ChequeraSerie -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraSerie));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraSerie -> error");
        }
    }

}
