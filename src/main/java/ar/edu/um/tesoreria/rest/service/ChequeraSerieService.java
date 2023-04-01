/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import ar.edu.um.tesoreria.rest.kotlin.model.ChequeraSerie;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ChequeraSerieException;
import ar.edu.um.tesoreria.rest.model.dto.DeudaChequera;
import ar.edu.um.tesoreria.rest.model.view.ChequeraAlta;
import ar.edu.um.tesoreria.rest.model.view.ChequeraIncompleta;
import ar.edu.um.tesoreria.rest.model.view.ChequeraKey;
import ar.edu.um.tesoreria.rest.repository.IChequeraSerieRepository;
import ar.edu.um.tesoreria.rest.service.view.ChequeraAltaService;
import ar.edu.um.tesoreria.rest.service.view.ChequeraIncompletaService;
import ar.edu.um.tesoreria.rest.service.view.ChequeraKeyService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ChequeraSerieService {

	@Autowired
	private IChequeraSerieRepository repository;

	@Autowired
	private ChequeraIncompletaService chequeraIncompletaService;

	@Autowired
	private ChequeraAltaService chequeraAltaService;

	@Autowired
	private ChequeraCuotaService chequeraCuotaService;

	@Autowired
	private TipoChequeraService tipoChequeraService;

	@Autowired
	private DebitoService debitoService;

	@Autowired
	private ChequeraKeyService chequeraKeyService;

	public List<ChequeraSerie> findAllByPersona(BigDecimal personaId, Integer documentoId) {
		return repository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, Sort.by("lectivoId").descending());
	}

	public List<ChequeraSerie> findAllByPersonaExtended(BigDecimal personaId, Integer documentoId) {
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
			Integer facultadId) {
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

	public List<ChequeraIncompleta> findAllIncompletas(Integer lectivoId, Integer facultadId, Integer geograficaId) {
		return chequeraIncompletaService.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId,
				geograficaId);
	}

	public List<ChequeraAlta> findAllAltas(Integer lectivoId, Integer facultadId, Integer geograficaId) {
		return chequeraAltaService.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId);
	}

	public List<ChequeraKey> findAllByCbu(String cbu, Integer debitoTipoId) {
		return chequeraKeyService.findAllByChequeraKey(debitoService.findAllByCbu(cbu, debitoTipoId).stream()
				.map(debito -> debito.chequeraKey()).collect(Collectors.toList()));
	}

	public List<ChequeraKey> findAllByVisa(String numeroTarjeta, Integer debitoTipoId) {
		return chequeraKeyService.findAllByChequeraKey(debitoService.findAllByVisa(numeroTarjeta, debitoTipoId).stream()
				.map(debito -> debito.chequeraKey()).collect(Collectors.toList()));
	}

	public ChequeraSerie findByChequeraId(Long chequeraId) {
		return repository.findByChequeraId(chequeraId).orElseThrow(() -> new ChequeraSerieException(chequeraId));
	}

	public ChequeraSerie findByChequeraIdExtended(Long chequeraId) {
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

	public ChequeraSerie findByUniqueExtended(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
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
						tipoChequeraService.findAllByClaseChequera(2).stream().map(tipo -> tipo.getTipoChequeraId())
								.collect(Collectors.toList()))
				.orElseThrow(
						() -> new ChequeraSerieException(personaId, documentoId, facultadId, lectivoId, geograficaId));
	}

	public ChequeraSerie findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
			BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId) {
		return repository
				.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraIdIn(
						personaId, documentoId, facultadId, lectivoId, geograficaId,
						tipoChequeraService.findAllByClaseChequera(1).stream().map(tipo -> tipo.getTipoChequeraId())
								.collect(Collectors.toList()))
				.orElseThrow(
						() -> new ChequeraSerieException(personaId, documentoId, facultadId, lectivoId, geograficaId));
	}

	public ChequeraSerie findLastPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadId(BigDecimal personaId,
			Integer documentoId, Integer facultadId) {
		return repository.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdInOrderByLectivoIdDesc(
				personaId, documentoId, facultadId, tipoChequeraService.findAllByClaseChequera(1).stream()
						.map(tipo -> tipo.getTipoChequeraId()).collect(Collectors.toList()));
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
							newChequeraSerie.getRetenida(), 0, BigDecimal.ZERO, null, null, null, null, null, null,
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
