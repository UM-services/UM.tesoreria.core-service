/**
 *
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import um.tesoreria.core.exception.ChequeraSerieException;
import um.tesoreria.core.kotlin.model.ChequeraSerie;
import um.tesoreria.core.kotlin.model.TipoChequera;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAltaFull;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.model.internal.FacultadSedeChequeraDto;
import um.tesoreria.core.model.view.ChequeraIncompleta;
import um.tesoreria.core.model.view.ChequeraKey;
import um.tesoreria.core.repository.ChequeraSerieRepository;
import um.tesoreria.core.service.view.ChequeraIncompletaService;
import um.tesoreria.core.service.view.ChequeraKeyService;
import um.tesoreria.core.service.view.ChequeraSerieAltaFullService;
import um.tesoreria.core.service.view.ChequeraSerieAltaService;

/**
 * @author daniel
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChequeraSerieService {

    private final ChequeraSerieRepository repository;
    private final ChequeraIncompletaService chequeraIncompletaService;
    private final ChequeraSerieAltaService chequeraSerieAltaService;
    private final ChequeraSerieAltaFullService chequeraSerieAltaFullService;
    private final TipoChequeraService tipoChequeraService;
    private final DebitoService debitoService;
    private final ChequeraKeyService chequeraKeyService;
    private final ChequeraImpresionCabeceraService chequeraImpresionCabeceraService;

    public List<ChequeraSerie> findAllByPersona(BigDecimal personaId, Integer documentoId) {
        return repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, Sort.by("lectivoId").descending());
    }

    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId) {
        log.debug("\n\nProcessing ChequeraSerieService.findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId\n\n");
        return repository.findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(personaId, documentoId, facultadId, lectivoId);
    }

    public List<ChequeraSerie> findAllByPersonaExtended(BigDecimal personaId, Integer documentoId, ChequeraCuotaService chequeraCuotaService) {
        return repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, Sort.by("lectivoId").descending())
                .stream()
                .peek(chequera -> setDeuda(chequera, chequeraCuotaService))
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

    public List<ChequeraSerie> findAllBySede(Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return repository.findAllByFacultadIdAndLectivoIdAndGeograficaId(facultadId, lectivoId, geograficaId);
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
                .peek(chequera -> setDeuda(chequera, chequeraCuotaService))
                .toList();
    }

    public List<ChequeraSerie> findAllByPersonaLectivo(BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        return repository.findAllByPersonaIdAndDocumentoIdAndLectivoId(personaId, documentoId, lectivoId)
                .stream()
                .peek(this::setUltimoEnvio)
                .toList();
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
        ChequeraSerie chequera = findByChequeraId(chequeraId);
        setDeuda(chequera, chequeraCuotaService);
        return chequera;
    }

    public ChequeraSerie findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        log.debug("\n\nProcessing ChequeraSerieService.findByUnique\n\n");
        var chequera = repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
        setUltimoEnvio(chequera);
        return chequera;
    }

    public ChequeraSerie findByUniqueExtended(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, ChequeraCuotaService chequeraCuotaService) {
        ChequeraSerie chequera = findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        setDeuda(chequera, chequeraCuotaService);
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
        return repository.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdIn(
                personaId,
                documentoId,
                facultadId,
                tipoChequeraService.findAllByClaseChequera(1).stream().map(TipoChequera::getTipoChequeraId).toList(),
                Sort
                        .by("lectivoId")
                        .descending()
                        .and(Sort
                                .by("chequeraSerieId")
                                .descending()
                        )
        ).orElseThrow(() -> new ChequeraSerieException(personaId, documentoId, facultadId));
    }

    @Transactional
    public ChequeraSerie setPayPerTic(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Byte flag) {
        var chequeraSerie = repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
        chequeraSerie.setFlagPayperTic(flag);
        return repository.save(chequeraSerie);
    }

    @Transactional
    public ChequeraSerie markSent(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        log.debug("Processing ChequeraSerieService.markSent");
        var chequeraSerie = repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .orElseThrow(() -> new ChequeraSerieException(facultadId, tipoChequeraId, chequeraSerieId));
        chequeraSerie.setEnviado((byte) 1);
        return repository.save(chequeraSerie);
    }

    public ChequeraSerie add(ChequeraSerie chequeraSerie) {
        log.debug("Processing ChequeraSerieService.add");
        return repository.save(chequeraSerie);
    }

    @Transactional
    public ChequeraSerie update(ChequeraSerie newChequeraSerie, Long chequeraId) {
        log.debug("Processing ChequeraSerieService.update for id {}", chequeraId);
        ChequeraSerie chequeraSerie = repository.findByChequeraId(chequeraId)
                .orElseThrow(() -> new ChequeraSerieException(chequeraId));

        chequeraSerie.setFacultadId(newChequeraSerie.getFacultadId());
        chequeraSerie.setTipoChequeraId(newChequeraSerie.getTipoChequeraId());
        chequeraSerie.setChequeraSerieId(newChequeraSerie.getChequeraSerieId());
        chequeraSerie.setPersonaId(newChequeraSerie.getPersonaId());
        chequeraSerie.setDocumentoId(newChequeraSerie.getDocumentoId());
        chequeraSerie.setLectivoId(newChequeraSerie.getLectivoId());
        chequeraSerie.setArancelTipoId(newChequeraSerie.getArancelTipoId());
        chequeraSerie.setCursoId(newChequeraSerie.getCursoId());
        chequeraSerie.setAsentado(newChequeraSerie.getAsentado());
        chequeraSerie.setGeograficaId(newChequeraSerie.getGeograficaId());
        chequeraSerie.setFecha(newChequeraSerie.getFecha());
        chequeraSerie.setCuotasPagadas(newChequeraSerie.getCuotasPagadas());
        chequeraSerie.setObservaciones(newChequeraSerie.getObservaciones());
        chequeraSerie.setAlternativaId(newChequeraSerie.getAlternativaId());
        chequeraSerie.setAlgoPagado(newChequeraSerie.getAlgoPagado());
        chequeraSerie.setTipoImpresionId(newChequeraSerie.getTipoImpresionId());
        chequeraSerie.setFlagPayperTic(newChequeraSerie.getFlagPayperTic());
        chequeraSerie.setUsuarioId(newChequeraSerie.getUsuarioId());
        chequeraSerie.setEnviado(newChequeraSerie.getEnviado());
        chequeraSerie.setRetenida(newChequeraSerie.getRetenida());
        chequeraSerie.setVersion(newChequeraSerie.getVersion());

        return repository.save(chequeraSerie);
    }

    @Transactional
    public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
                                                                         Long chequeraSerieId) {
        repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
                chequeraSerieId);
    }

    public List<FacultadSedeChequeraDto> resumenLectivo(Integer lectivoId) {
        return repository.findAllFacultadSedeByLectivo(lectivoId);
    }

    private void setDeuda(ChequeraSerie chequera, ChequeraCuotaService chequeraCuotaService) {
        var deuda = chequeraCuotaService.calculateDeuda(
                chequera.getFacultadId(),
                chequera.getTipoChequeraId(),
                chequera.getChequeraSerieId()
        );
        chequera.setCuotasDeuda(deuda.getCuotas());
        chequera.setImporteDeuda(deuda.getDeuda());
    }

    private void setUltimoEnvio(ChequeraSerie chequera) {
        try {
            var ultimoEnvio = Optional.ofNullable(
                    chequeraImpresionCabeceraService.findLastByUnique(
                            chequera.getFacultadId(),
                            chequera.getTipoChequeraId(),
                            chequera.getChequeraSerieId()
                    )
            )
            .map(cabecera -> Objects.requireNonNull(cabecera.getFecha()).plusHours(-3))
            .orElse(null);

            chequera.setUltimoEnvio(ultimoEnvio);
        } catch (Exception e) {
            chequera.setUltimoEnvio(null);
            log.error("No se encontró último envío para la chequera: {}", chequera.getChequeraId(), e);
        }
    }

}
