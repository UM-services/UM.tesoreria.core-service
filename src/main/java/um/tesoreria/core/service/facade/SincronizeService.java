/**
 * 
 */
package um.tesoreria.core.service.facade;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CarreraException;
import um.tesoreria.core.exception.DomicilioException;
import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.exception.InfoLdapException;
import um.tesoreria.core.exception.LegajoException;
import um.tesoreria.core.exception.MatriculaException;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.exception.PlanException;
import um.tesoreria.core.exception.UsuarioLdapException;
import um.tesoreria.core.exception.view.ChequeraClaseException;
import um.tesoreria.core.extern.consumer.CarreraFacultadConsumer;
import um.tesoreria.core.extern.consumer.InscripcionDetalleFacultadConsumer;
import um.tesoreria.core.extern.consumer.InscripcionFacultadConsumer;
import um.tesoreria.core.extern.consumer.LegajoFacultadConsumer;
import um.tesoreria.core.extern.consumer.PersonaFacultadConsumer;
import um.tesoreria.core.extern.consumer.PlanFacultadConsumer;
import um.tesoreria.core.extern.consumer.PreInscripcionFacultadConsumer;
import um.tesoreria.core.extern.model.kotlin.*;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.model.InfoLdap;
import um.tesoreria.core.model.Legajo;
import um.tesoreria.core.model.Matricula;
import um.tesoreria.core.model.Plan;
import um.tesoreria.core.model.UsuarioLdap;
import um.tesoreria.core.model.view.ChequeraClase;
import um.tesoreria.core.service.CarreraService;
import um.tesoreria.core.service.ChequeraSerieService;
import um.tesoreria.core.service.DomicilioService;
import um.tesoreria.core.service.FacultadService;
import um.tesoreria.core.service.InfoLdapService;
import um.tesoreria.core.service.LegajoService;
import um.tesoreria.core.service.MatriculaService;
import um.tesoreria.core.service.PersonaService;
import um.tesoreria.core.service.PlanService;
import um.tesoreria.core.service.UsuarioLdapService;
import um.tesoreria.core.service.view.ChequeraClaseService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class SincronizeService {

	@Autowired
	private FacultadService facultadService;

	@Autowired
	private PersonaService personaService;

	@Autowired
	private UsuarioLdapService usuarioLdapService;

	@Autowired
	private ChequeraSerieService chequeraSerieService;

	@Autowired
	private ChequeraClaseService chequeraClaseService;

	@Autowired
	private InfoLdapService infoLdapService;

	@Autowired
	private DomicilioService domicilioService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private PlanService planService;

	@Autowired
	private LegajoService legajoService;

	@Autowired
	private InscripcionFacultadConsumer inscripcionFacultadConsumer;

	@Autowired
	private InscripcionDetalleFacultadConsumer inscripcionDetalleFacultadConsumer;

	@Autowired
	private PersonaFacultadConsumer personaFacultadConsumer;

	@Autowired
	private PreInscripcionFacultadConsumer preInscripcionFacultadConsumer;

	@Autowired
	private LegajoFacultadConsumer legajoFacultadConsumer;

	@Autowired
	private PlanFacultadConsumer planFacultadConsumer;

	@Autowired
	private CarreraFacultadConsumer carreraFacultadConsumer;

	@Transactional
	public void sincronizeMatricula(Integer lectivoId, Integer facultadId, MatriculaService matriculaService) throws CloneNotSupportedException {
		Facultad facultad = null;
		try {
			facultad = facultadService.findByFacultadId(facultadId);
		} catch (FacultadException e) {
			return;
		}
		List<Matricula> matriculas = new ArrayList<>();
		for (InscripcionFacultad inscripcion : inscripcionFacultadConsumer.findAllByLectivo(facultad.getApiserver(),
				facultad.getApiport(), facultadId, lectivoId)) {
			List<InscripcionDetalleFacultad> detalles = inscripcionDetalleFacultadConsumer.findAllByPersona(
					facultad.getApiserver(), facultad.getApiport(), inscripcion.getPersonaId(),
					inscripcion.getDocumentoId(), inscripcion.getFacultadId(), inscripcion.getLectivoId());
			// Verifica usuarioldap
			UsuarioLdap usuarioldap = null;
			try {
				usuarioldap = usuarioLdapService.findByDocumento(inscripcion.getPersonaId());
			} catch (UsuarioLdapException e) {
				usuarioldap = new UsuarioLdap(null, inscripcion.getPersonaId(), "alta", "pendiente", facultad.getDsn());
				usuarioldap = usuarioLdapService.add(usuarioldap);
			}
			Integer[] clases = { 2, 5 };
			ChequeraClase serie = null;
			try {
				serie = chequeraClaseService
						.findFirstByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClaseChequeraIdIn(
								inscripcion.getFacultadId(), inscripcion.getPersonaId(), inscripcion.getDocumentoId(),
								inscripcion.getLectivoId(), Arrays.asList(clases));
			} catch (ChequeraClaseException e) {
				serie = new ChequeraClase();
			}
			// Verificar persona
			Persona persona = null;
			try {
				persona = personaService.findByUnique(inscripcion.getPersonaId(), inscripcion.getDocumentoId());
			} catch (PersonaException e) {
				persona = personaFacultadConsumer.findByUnique(facultad.getApiserver(), facultad.getApiport(),
						inscripcion.getPersonaId(), inscripcion.getDocumentoId());
				persona.setUniqueId(null);
				persona = personaService.add(persona);
			}
			// Verificar matricula
			Matricula matricula = null;
			try {
				matricula = matriculaService.findByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClasechequeraIdIn(
						inscripcion.getFacultadId(), inscripcion.getPersonaId(), inscripcion.getDocumentoId(),
						inscripcion.getLectivoId(), Arrays.asList(clases));
			} catch (MatriculaException e) {
				matricula = new Matricula();
			}
			Matricula matricula_old = (Matricula) matricula.clone();
			Integer clasechequeraId = 2;
			if (facultadId == 15)
				clasechequeraId = 5;
			if (matricula.getMatriculaId() == null) {
				matricula = new Matricula(null, inscripcion.getFecha(), facultadId, inscripcion.getPersonaId(),
						inscripcion.getDocumentoId(), lectivoId, inscripcion.getGeograficaId(), inscripcion.getPlanId(),
						inscripcion.getCarreraId(), null, null, inscripcion.getCurso(), detalles.size(), 0, (byte) 1,
						inscripcion.getProvisoria(), clasechequeraId);
			}
			if (serie.getChequeraId() != null) {
				matricula.setChequerapendiente((byte) 0);
				matricula.setTipochequeraId(serie.getTipoChequeraId());
				matricula.setChequeraserieId(serie.getChequeraSerieId());
			}
			if (!matricula_old.equals(matricula))
				matriculas.add(matricula);
		}
//		Comienzo análisis preuniversitario
		Integer offset = 1;
		if (OffsetDateTime.now().getMonthValue() < 7) {
			offset = 0;
		}
		for (PreInscripcionFacultad preInscripcion : preInscripcionFacultadConsumer.findAllByLectivo(
				facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), lectivoId + offset)) {
			Integer[] clases = { 1 };
			ChequeraClase serie = null;
			try {
				serie = chequeraClaseService
						.findFirstByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClaseChequeraIdIn(
								preInscripcion.getFacultadId(), preInscripcion.getPersonaId(),
								preInscripcion.getDocumentoId(), preInscripcion.getLectivoId() - 1,
								Arrays.asList(clases));
			} catch (ChequeraClaseException e) {
				serie = new ChequeraClase();
			}
			// Verificar persona
			Persona persona = null;
			try {
				persona = personaService.findByUnique(preInscripcion.getPersonaId(), preInscripcion.getDocumentoId());
			} catch (PersonaException e) {
				persona = personaFacultadConsumer.findByUnique(facultad.getApiserver(), facultad.getApiport(),
						preInscripcion.getPersonaId(), preInscripcion.getDocumentoId());
				persona.setUniqueId(null);
				persona = personaService.add(persona);
			}
			// Verificar matricula
			Matricula matricula = null;
			try {
				matricula = matriculaService.findByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoIdAndClasechequeraId(
						preInscripcion.getFacultadId(), preInscripcion.getPersonaId(), preInscripcion.getDocumentoId(),
						preInscripcion.getLectivoId(), 1);
			} catch (MatriculaException e) {
				matricula = new Matricula();
			}
			Matricula matricula_old = (Matricula) matricula.clone();
			if (matricula.getMatriculaId() == null) {
				LegajoFacultad legajo = legajoFacultadConsumer.findByPersona(facultad.getApiserver(),
						facultad.getApiport(), preInscripcion.getPersonaId(), preInscripcion.getDocumentoId(),
						preInscripcion.getFacultadId());
				matricula = new Matricula(null, preInscripcion.getFecha(), facultadId, preInscripcion.getPersonaId(),
						preInscripcion.getDocumentoId(), preInscripcion.getLectivoId(),
						preInscripcion.getGeograficaId(), legajo.getPlanId(), legajo.getCarreraId(), null, null, 0, 0,
						0, (byte) 1, (byte) 0, 1);
			}
			if (serie.getChequeraId() != null) {
				matricula.setChequerapendiente((byte) 0);
				matricula.setTipochequeraId(serie.getTipoChequeraId());
				matricula.setChequeraserieId(serie.getChequeraSerieId());
			}
			if (!matricula_old.equals(matricula))
				matriculas.add(matricula);
		}
		if (!matriculas.isEmpty()) {
			matriculas = matriculaService.saveAll(matriculas);
		}
	}

	@Transactional
	public void sincronizeInstitucional(Integer lectivoId, Integer facultadId) {
		for (ChequeraSerie serie : chequeraSerieService.findAllByLectivoIdAndFacultadId(lectivoId, facultadId)) {
			InfoLdap usuario = null;
			try {
				usuario = infoLdapService.findByPersonaId(serie.getPersonaId());
			} catch (InfoLdapException e) {
				usuario = new InfoLdap();
			}
			if (usuario.getPersonaId() != null) {
				Domicilio domicilio = null;
				try {
					domicilio = domicilioService.findFirstByPersonaId(serie.getPersonaId());

				} catch (DomicilioException e) {
					domicilio = new Domicilio();
				}
				domicilio.setEmailInstitucional(usuario.getEmailInstitucional());
				if (domicilio.getDomicilioId() == null) {
					domicilio.setPersonaId(serie.getPersonaId());
					domicilio.setDocumentoId(serie.getDocumentoId());
					domicilioService.add(domicilio, true);
				} else {
					domicilioService.update(domicilio, domicilio.getDomicilioId(), true);
				}
			}
		}
	}

	@Transactional
	public void sincronizeCarreraAlumno(Integer facultadId, BigDecimal personaId, Integer documentoId) {
		Facultad facultad = null;
		try {
			facultad = facultadService.findByFacultadId(facultadId);
		} catch (FacultadException e) {
			return;
		}
		LegajoFacultad legajoFacultad = null;
		try {
			legajoFacultad = legajoFacultadConsumer.findByPersona(facultad.getApiserver(), facultad.getApiport(),
					personaId, documentoId, facultadId);
		} catch (Exception e) {
			log.debug("Sin LEGAJO");
			return;
		}
		PlanFacultad planFacultad = null;
		try {
			planFacultad = planFacultadConsumer.findByUnique(facultad.getApiserver(), facultad.getApiport(),
					legajoFacultad.getFacultadId(), legajoFacultad.getPlanId());
		} catch (Exception e) {
			log.debug("Sin PLAN");
			return;
		}
		CarreraFacultad carreraFacultad = null;
		try {
			carreraFacultad = carreraFacultadConsumer.findByUnique(facultad.getApiserver(), facultad.getApiport(),
					legajoFacultad.getFacultadId(), legajoFacultad.getPlanId(), legajoFacultad.getCarreraId());
		} catch (Exception e) {
			log.debug("Sin CARRERA");
			return;
		}
		Plan plan = null;
		try {
			plan = planService.findByFacultadIdAndPlanId(planFacultad.getFacultadId(), planFacultad.getPlanId());
		} catch (PlanException e) {
			plan = new Plan(null, planFacultad.getFacultadId(), planFacultad.getPlanId(), planFacultad.getNombre(),
					planFacultad.getFecha(), planFacultad.getPublicar());
			planService.add(plan);
		}
		Carrera carrera = null;
		try {
			carrera = carreraService.findByFacultadIdAndPlanIdAndCarreraId(carreraFacultad.getFacultadId(),
					carreraFacultad.getPlanId(), carreraFacultad.getCarreraId());
		} catch (CarreraException e) {
			carrera = new Carrera(null, carreraFacultad.getFacultadId(), carreraFacultad.getPlanId(),
					carreraFacultad.getCarreraId(), carreraFacultad.getNombre(), carreraFacultad.getIniciales(),
					carreraFacultad.getTitulo(), carreraFacultad.getTrabajoFinal(), carreraFacultad.getResolucion(),
					carreraFacultad.getChequeraUnica(), carreraFacultad.getBloqueId(),
					carreraFacultad.getObligatorias(), carreraFacultad.getOptativas(), carreraFacultad.getVigente());
			carreraService.add(carrera);
		}
		Long legajoId = null;
		Boolean update = true;
		try {
			Legajo legajo = legajoService.findByFacultadIdAndPersonaIdAndDocumentoId(facultadId, personaId,
					documentoId);
			legajoId = legajo.getLegajoId();
			if (legajoFacultad.getPlanId() == legajo.getPlanId()
					&& legajoFacultad.getCarreraId() == legajo.getCarreraId()) {
				update = false;
			}
		} catch (LegajoException e) {
			log.debug("Sin legajo");
		}
		if (legajoFacultad.getLectivoId() == 0) {
			legajoFacultad.setLectivoId(null);
		}
		if (update) {
			Legajo legajo = new Legajo(legajoId, personaId, documentoId, facultadId, legajoFacultad.getNumeroLegajo(),
					legajoFacultad.getFecha(), legajoFacultad.getLectivoId(), legajoFacultad.getPlanId(),
					legajoFacultad.getCarreraId(), (byte) 1, legajoFacultad.getGeograficaId(),
					legajoFacultad.getContrasenha(), legajoFacultad.getIntercambio());
			legajo = legajoService.add(legajo);
		}
	}

	@Transactional
	public void sincronizeCarrera(Integer facultadId) {
		Facultad facultad = null;
		try {
			facultad = facultadService.findByFacultadId(facultadId);
		} catch (FacultadException e) {
			return;
		}
		List<PlanFacultad> planesFacultad = planFacultadConsumer.findAll(facultad.getApiserver(),
				facultad.getApiport());
		List<CarreraFacultad> carrerasFacultad = carreraFacultadConsumer.findAll(facultad.getApiserver(),
				facultad.getApiport());
		Map<String, Plan> planes = planService.findAll().stream()
				.collect(Collectors.toMap(Plan::getPlanKey, plan -> plan));
		Map<String, Carrera> carreras = carreraService.findAll().stream()
				.collect(Collectors.toMap(Carrera::getCarreraKey, carrera -> carrera));

		// Actualiza Planes
		for (PlanFacultad planFacultad : planesFacultad) {
			if (planes.containsKey(planFacultad.getPlanKey())) {
				Plan plan = planes.get(planFacultad.getPlanKey());
				plan = new Plan(plan.getUniqueId(), planFacultad.getFacultadId(), planFacultad.getPlanId(),
						planFacultad.getNombre(), planFacultad.getFecha(), planFacultad.getPublicar());
			} else {
				Plan plan = new Plan(null, planFacultad.getFacultadId(), planFacultad.getPlanId(),
						planFacultad.getNombre(), planFacultad.getFecha(), planFacultad.getPublicar());
				planes.put(plan.getPlanKey(), plan);
			}
		}
		planService.saveAll(new ArrayList<Plan>(planes.values()));

		// Actualiza Carreras
		for (CarreraFacultad carreraFacultad : carrerasFacultad) {
			if (carreras.containsKey(carreraFacultad.getCarreraKey())) {
				Carrera carrera = carreras.get(carreraFacultad.getCarreraKey());
				carrera = new Carrera(carrera.getUniqueId(), carreraFacultad.getFacultadId(),
						carreraFacultad.getPlanId(), carreraFacultad.getCarreraId(), carreraFacultad.getNombre(),
						carreraFacultad.getIniciales(), carreraFacultad.getTitulo(), carreraFacultad.getTrabajoFinal(),
						carreraFacultad.getResolucion(), carreraFacultad.getChequeraUnica(),
						carreraFacultad.getBloqueId(), carreraFacultad.getObligatorias(),
						carreraFacultad.getOptativas(), carreraFacultad.getVigente());
			} else {
				Carrera carrera = new Carrera(null, carreraFacultad.getFacultadId(), carreraFacultad.getPlanId(),
						carreraFacultad.getCarreraId(), carreraFacultad.getNombre(), carreraFacultad.getIniciales(),
						carreraFacultad.getTitulo(), carreraFacultad.getTrabajoFinal(), carreraFacultad.getResolucion(),
						carreraFacultad.getChequeraUnica(), carreraFacultad.getBloqueId(),
						carreraFacultad.getObligatorias(), carreraFacultad.getOptativas(),
						carreraFacultad.getVigente());
				carreras.put(carrera.getCarreraKey(), carrera);
			}
		}
		carreraService.saveAll(new ArrayList<>(carreras.values()));
	}

}
