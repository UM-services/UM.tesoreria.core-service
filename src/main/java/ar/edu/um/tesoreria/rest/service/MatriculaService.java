/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import ar.edu.um.tesoreria.rest.exception.MatriculaNotFoundException;
import ar.edu.um.tesoreria.rest.exception.view.ChequeraClaseNotFoundException;
import ar.edu.um.tesoreria.rest.extern.model.BajaFacultad;
import ar.edu.um.tesoreria.rest.extern.model.CarreraFacultad;
import ar.edu.um.tesoreria.rest.extern.model.InscripcionFacultad;
import ar.edu.um.tesoreria.rest.extern.model.LegajoFacultad;
import ar.edu.um.tesoreria.rest.extern.model.PreInscripcionFacultad;
import ar.edu.um.tesoreria.rest.extern.model.TituloEntregaFacultad;
import ar.edu.um.tesoreria.rest.model.Facultad;
import ar.edu.um.tesoreria.rest.model.Lectivo;
import ar.edu.um.tesoreria.rest.model.Matricula;
import ar.edu.um.tesoreria.rest.model.view.ChequeraClase;
import ar.edu.um.tesoreria.rest.model.view.ChequeraCuotaPersona;
import ar.edu.um.tesoreria.rest.repository.IMatriculaRepository;
import ar.edu.um.tesoreria.rest.service.facade.SincronizeService;
import ar.edu.um.tesoreria.rest.service.view.ChequeraClaseService;
import ar.edu.um.tesoreria.rest.service.view.ChequeraCuotaPersonaService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class MatriculaService {

	@Autowired
	private IMatriculaRepository repository;

	@Autowired
	private LectivoService lectivoservice;

	@Autowired
	private FacultadService facultadService;

	@Autowired
	private SincronizeService sincronizeservice;

	@Autowired
	private ChequeraClaseService chequeraclaseservice;

	@Autowired
	private ChequeraCuotaPersonaService chequeraCuotaPersonaService;

	public List<Matricula> findPendientes() {
		return repository.findAllByChequerapendiente((byte) 1,
				Sort.by("lectivoId").ascending().and(Sort.by("facultadId").ascending())
						.and(Sort.by("geograficaId").ascending()).and(Sort.by("planId").ascending())
						.and(Sort.by("carreraId").and(Sort.by("fecha").ascending())));
	}

	public Matricula findByMatriculaId(Long matriculaId) {
		return repository.findByMatriculaId(matriculaId).orElseThrow(() -> new MatriculaNotFoundException(matriculaId));
	}

	public Matricula findByUnique(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId,
			Integer clasechequeraId) {
		return repository
				.findByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClasechequeraId(facultadId, personaId,
						documentoId, lectivoId, clasechequeraId)
				.orElseThrow(() -> new MatriculaNotFoundException(facultadId, personaId, documentoId, lectivoId,
						clasechequeraId));
	}

	public Matricula findByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClasechequeraIdIn(Integer facultadId,
			BigDecimal personaId, Integer documentoId, Integer lectivoId, List<Integer> clases) {
		return repository.findByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClasechequeraIdIn(facultadId,
				personaId, documentoId, lectivoId, clases).orElseThrow(() -> new MatriculaNotFoundException());
	}

	public Matricula findByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClasechequeraId(Integer facultadId,
			BigDecimal personaId, Integer documentoId, Integer lectivoId, Integer clasechequeraId) {
		return repository
				.findByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClasechequeraId(facultadId, personaId,
						documentoId, lectivoId, clasechequeraId)
				.orElseThrow(() -> new MatriculaNotFoundException(facultadId, personaId, documentoId, lectivoId,
						clasechequeraId));
	}

	@Transactional
	public Matricula add(Matricula matricula) {
		repository.save(matricula);
		return matricula;
	}

	@Transactional
	public Matricula update(Matricula newmatricula, Long matriculaId) {
		return repository.findByMatriculaId(matriculaId).map(matricula -> {
			matricula = new Matricula(matriculaId, matricula.getFecha(), matricula.getFacultadId(),
					matricula.getPersonaId(), matricula.getDocumentoId(), matricula.getLectivoId(),
					newmatricula.getGeograficaId(), newmatricula.getPlanId(), newmatricula.getCarreraId(),
					newmatricula.getTipochequeraId(), newmatricula.getChequeraserieId(), newmatricula.getCurso(),
					newmatricula.getCantidadmateriasmatricula(), newmatricula.getCantidadmateriascurso(),
					newmatricula.getChequerapendiente(), newmatricula.getProvisoria(),
					newmatricula.getClasechequeraId());
			repository.save(matricula);
			return matricula;
		}).orElseThrow(() -> new MatriculaNotFoundException(matriculaId));
	}

	@Transactional
	public List<Matricula> saveAll(List<Matricula> matriculas) {
		matriculas = repository.saveAll(matriculas);
		return matriculas;
	}

	@Transactional
	public void updateAndDepurate() throws CloneNotSupportedException {
		log.debug("Depurando matrículas provisorias y de ciclos anteriores");
		Lectivo lectivo_actual = lectivoservice.findByLectivoId(OffsetDateTime.now().getYear() - 1989);
		log.debug("Lectivo Actual -> " + lectivo_actual);
		if (lectivo_actual.getLectivoId() > 0) {
			log.debug("Sincronizando Matriculas");
			for (Facultad facultad : facultadService.findFacultades()) {
				log.debug("Sincronizando Facultad -> " + facultad);
				sincronizeservice.sincronizeMatricula(lectivo_actual.getLectivoId(), facultad.getFacultadId());
			}
			depurate(lectivo_actual.getLectivoId());
		}
	}

	@Transactional
	public void depurate(Integer lectivoId) {
		log.debug("Elimina matrículas de ciclos anteriores");
		repository.deleteAllByLectivoIdLessThan(lectivoId);
		log.debug("Elimina matrículas provisorias");
		repository.deleteAllByProvisoria((byte) 1);
		Lectivo lectivo = lectivoservice.findByLectivoId(lectivoId);
		Integer bajas = 0;
		Integer legajos = 0;
		Integer unicas = 0;
		Integer matriculas_eliminadas = 0;
		Integer otros = 0;
		Integer titulados = 0;
		Integer preinscripciones_eliminadas = 0;
		for (Matricula matricula : repository.findAllByChequerapendiente((byte) 1, Sort.by("fecha").ascending())) {
			log.debug("Matricula pendiente -> " + matricula);
//			Verifica si hay una chequera de otro ciclo lectivo que abarque este ciclo
			Optional<ChequeraCuotaPersona> chequeraCuotaPersona = chequeraCuotaPersonaService
					.findByPersonaIdAndDocumentoIdAndFacultadIdAndAnhoAndMes(matricula.getPersonaId(),
							matricula.getDocumentoId(), matricula.getFacultadId(), lectivo.getFechaInicio().getYear(),
							lectivo.getFechaInicio().getMonthValue());
			if (chequeraCuotaPersona.isPresent()) {
				log.debug("ChequeraCuotaPersona -> " + chequeraCuotaPersona.get());
				repository.deleteByMatriculaId(matricula.getMatriculaId());
				continue;
			}
			RestTemplate restTemplate = new RestTemplate();
			Facultad facultad = facultadService.findByFacultadId(matricula.getFacultadId());
			if (!facultad.getApiserver().equals("")) {
//				Verifica si el alumno es de intercambio
				String url = "http://" + facultad.getApiserver() + ":" + facultad.getApiport() + "/legajo/persona/"
						+ matricula.getPersonaId() + "/" + matricula.getDocumentoId() + "/" + matricula.getFacultadId();
				LegajoFacultad legajo = null;
				try {
					legajo = restTemplate.getForObject(url, LegajoFacultad.class);
				} catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
					if (HttpStatus.INTERNAL_SERVER_ERROR.equals(httpClientOrServerExc.getStatusCode())) {
						log.debug("Status 500");
						repository.deleteByMatriculaId(matricula.getMatriculaId());
						legajos++;
						continue;
					} else {
						log.debug("Status otro");
					}
				}
				if (legajo.getIntercambio() == 1) {
					log.debug("Legajo -> " + legajo);
					repository.deleteByMatriculaId(matricula.getMatriculaId());
					continue;
				}
				if (matricula.getClasechequeraId() == 2) {
//					Verifica si el alumno está de baja
					url = "http://" + facultad.getApiserver() + ":" + facultad.getApiport() + "/baja/unique/"
							+ matricula.getFacultadId() + "/" + matricula.getPersonaId() + "/"
							+ matricula.getDocumentoId() + "/" + matricula.getLectivoId();
					try {
						BajaFacultad baja = restTemplate.getForObject(url, BajaFacultad.class);
						log.debug("Baja -> " + baja);
						bajas++;
						repository.deleteByMatriculaId(matricula.getMatriculaId());
						continue;
					} catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
						if (HttpStatus.INTERNAL_SERVER_ERROR.equals(httpClientOrServerExc.getStatusCode())) {
							log.debug("Status 500");
						} else {
							log.debug("Status otro");
						}
					}
//					Verifica si el alumno tiene datos de título de egresado
					url = "http://" + facultad.getApiserver() + ":" + facultad.getApiport() + "/tituloentrega/unique/"
							+ matricula.getPersonaId() + "/" + matricula.getDocumentoId() + "/"
							+ matricula.getFacultadId() + "/" + matricula.getPlanId() + "/" + matricula.getCarreraId();
					try {
						TituloEntregaFacultad tituloentrega = restTemplate.getForObject(url,
								TituloEntregaFacultad.class);
						log.debug("TituloEntrega -> " + tituloentrega);
						titulados++;
						repository.deleteByMatriculaId(matricula.getMatriculaId());
						continue;
					} catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
						if (HttpStatus.INTERNAL_SERVER_ERROR.equals(httpClientOrServerExc.getStatusCode())) {
							log.debug("Status 500");
						} else {
							log.debug("Status otro");
						}
					}
//					Verifica si se eliminó la inscripción en la facultad
					url = "http://" + facultad.getApiserver() + ":" + facultad.getApiport() + "/inscripcion/unique/"
							+ matricula.getFacultadId() + "/" + matricula.getPersonaId() + "/"
							+ matricula.getDocumentoId() + "/" + matricula.getLectivoId();
					try {
						InscripcionFacultad inscripcion = restTemplate.getForObject(url, InscripcionFacultad.class);
//						Verifica si la inscripción se marcó como provisoria
						if (inscripcion.getProvisoria() == 1) {
							log.debug("Inscripcion -> " + inscripcion);
							matriculas_eliminadas++;
							repository.deleteByMatriculaId(matricula.getMatriculaId());
							continue;
						}
						if (inscripcion.getCarreraId() > 0) {
//						Verifica si la carrera tiene chequera única
							url = "http://" + facultad.getApiserver() + ":" + facultad.getApiport() + "/carrera/unique/"
									+ inscripcion.getFacultadId() + "/" + inscripcion.getPlanId() + "/"
									+ inscripcion.getCarreraId();
							CarreraFacultad carrera = restTemplate.getForObject(url, CarreraFacultad.class);
							if (carrera.getChequeraunica() == 1) {
								try {
									ChequeraClase chequera = chequeraclaseservice.findChequeraGradoUnica(
											matricula.getFacultadId(), matricula.getPersonaId(),
											matricula.getDocumentoId(), matricula.getLectivoId());
									log.debug("ChequeraClase -> " + chequera);
									unicas++;
									repository.deleteByMatriculaId(matricula.getMatriculaId());
									continue;
								} catch (ChequeraClaseNotFoundException e) {
									log.debug(e.getMessage());
								}
							}
						}
					} catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
						if (HttpStatus.INTERNAL_SERVER_ERROR.equals(httpClientOrServerExc.getStatusCode())) {
							log.debug("Status 500");
							repository.deleteByMatriculaId(matricula.getMatriculaId());
							matriculas_eliminadas++;
						} else {
							log.debug("Status otro");
							otros++;
						}
					}
				}
				if (matricula.getClasechequeraId() == 1) {
//					Verifica si se eliminó la inscripción del preuniversitario en la facultad
					url = "http://" + facultad.getApiserver() + ":" + facultad.getApiport()
							+ "/preinscripcion/personalectivo/" + matricula.getFacultadId() + "/"
							+ matricula.getPersonaId() + "/" + matricula.getDocumentoId() + "/"
							+ matricula.getLectivoId();
					log.debug(url);
					try {
						restTemplate.getForObject(url, PreInscripcionFacultad.class);
					} catch (HttpClientErrorException | HttpServerErrorException httpClientOrServerExc) {
						if (HttpStatus.INTERNAL_SERVER_ERROR.equals(httpClientOrServerExc.getStatusCode())) {
							log.debug("Status 500");
							repository.deleteByMatriculaId(matricula.getMatriculaId());
							preinscripciones_eliminadas++;
						} else {
							log.debug("Status otro");
							otros++;
						}
					}
				}
			}
		}
		log.debug("Bajas " + bajas);
		log.debug("Legajos {}", legajos);
		log.debug("Unicas " + unicas);
		log.debug("Matriculas Eliminadas " + matriculas_eliminadas);
		log.debug("PreInscripciones Eliminadas " + preinscripciones_eliminadas);
		log.debug("Otros " + otros);
		log.debug("Titulados " + titulados);
	}

}
