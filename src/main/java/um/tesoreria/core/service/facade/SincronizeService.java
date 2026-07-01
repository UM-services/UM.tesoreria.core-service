/**
 * 
 */
package um.tesoreria.core.service.facade;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CarreraException;
import um.tesoreria.core.hexagonal.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.exception.InfoLdapException;
import um.tesoreria.core.exception.LegajoException;
import um.tesoreria.core.exception.PlanException;
import um.tesoreria.core.extern.consumer.CarreraFacultadConsumer;
import um.tesoreria.core.extern.consumer.InscripcionDetalleFacultadConsumer;
import um.tesoreria.core.extern.consumer.InscripcionFacultadConsumer;
import um.tesoreria.core.extern.consumer.LegajoFacultadConsumer;
import um.tesoreria.core.extern.consumer.PersonaFacultadConsumer;
import um.tesoreria.core.extern.consumer.PlanFacultadConsumer;
import um.tesoreria.core.extern.consumer.PreInscripcionFacultadConsumer;
import um.tesoreria.core.extern.model.kotlin.*;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.entity.DomicilioEntity;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.model.InfoLdap;
import um.tesoreria.core.service.CarreraService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.facultad.application.service.FacultadService;
import um.tesoreria.core.service.InfoLdapService;
import um.tesoreria.core.service.LegajoService;
import um.tesoreria.core.hexagonal.persona.application.service.PersonaService;
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
@RequiredArgsConstructor
public class SincronizeService {

	private final FacultadService facultadService;
	private final PersonaService personaService;
	private final UsuarioLdapService usuarioLdapService;
	private final ChequeraSerieService chequeraSerieService;
	private final ChequeraClaseService chequeraClaseService;
	private final InfoLdapService infoLdapService;
	private final DomicilioService domicilioService;
	private final CarreraService carreraService;
	private final PlanService planService;
	private final LegajoService legajoService;
	private final InscripcionFacultadConsumer inscripcionFacultadConsumer;
	private final InscripcionDetalleFacultadConsumer inscripcionDetalleFacultadConsumer;
	private final PersonaFacultadConsumer personaFacultadConsumer;
	private final PreInscripcionFacultadConsumer preInscripcionFacultadConsumer;
	private final LegajoFacultadConsumer legajoFacultadConsumer;
	private final PlanFacultadConsumer planFacultadConsumer;
	private final CarreraFacultadConsumer carreraFacultadConsumer;

	@Transactional
	public void sincronizeInstitucional(Integer lectivoId, Integer facultadId) {
		for (ChequeraSerieEntity serie : chequeraSerieService.findAllByLectivoIdAndFacultadId(lectivoId, facultadId)) {
			InfoLdap usuario;
			try {
				usuario = infoLdapService.findByPersonaId(serie.getPersonaId());
			} catch (InfoLdapException e) {
				usuario = new InfoLdap();
			}
			if (usuario.getPersonaId() != null) {
				Domicilio domicilio = domicilioService.findFirstByPersonaId(serie.getPersonaId())
						.orElse(new Domicilio());
				domicilio.setEmailInstitucional(usuario.getEmailInstitucional());
				if (domicilio.getDomicilioId() == null) {
					domicilio.setPersonaId(serie.getPersonaId());
					domicilio.setDocumentoId(serie.getDocumentoId());
					domicilioService.create(domicilio);
				} else {
					domicilioService.update(domicilio.getDomicilioId(), domicilio);
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
					carreraFacultad.getObligatorias(), carreraFacultad.getOptativas(), carreraFacultad.getVigente(), null);
			carreraService.add(carrera);
		}
		Long legajoId = null;
		boolean update = true;
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
		assert  legajoFacultad.getLectivoId() != null;
		if (legajoFacultad.getLectivoId() == 0) {
			legajoFacultad.setLectivoId(null);
		}
		if (update) {
			assert legajoFacultad.getNumeroLegajo() != null;
			Legajo legajo = new Legajo(legajoId, personaId, documentoId, facultadId, legajoFacultad.getNumeroLegajo(),
					legajoFacultad.getFecha(), legajoFacultad.getLectivoId(), legajoFacultad.getPlanId(),
					legajoFacultad.getCarreraId(), (byte) 1, legajoFacultad.getGeograficaId(),
					legajoFacultad.getContrasenha(), legajoFacultad.getIntercambio(), null);
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
						carreraFacultad.getOptativas(), carreraFacultad.getVigente(), null);
			} else {
				Carrera carrera = new Carrera(null,
						carreraFacultad.getFacultadId(),
						carreraFacultad.getPlanId(),
						carreraFacultad.getCarreraId(),
						Objects.requireNonNull(carreraFacultad.getNombre()),
						Objects.requireNonNull(carreraFacultad.getIniciales()),
                        Objects.requireNonNull(carreraFacultad.getTitulo()),
						carreraFacultad.getTrabajoFinal(),
                        Objects.requireNonNull(carreraFacultad.getResolucion()),
						carreraFacultad.getChequeraUnica(),
						carreraFacultad.getBloqueId(),
						carreraFacultad.getObligatorias(),
						carreraFacultad.getOptativas(),
						carreraFacultad.getVigente(),
						null);
				carreras.put(carrera.getCarreraKey(), carrera);
			}
		}
		carreraService.saveAll(new ArrayList<>(carreras.values()));
	}

}
