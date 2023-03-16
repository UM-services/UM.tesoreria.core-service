/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ChequeraSerieException;
import ar.edu.um.tesoreria.rest.exception.PersonaException;
import ar.edu.um.tesoreria.rest.extern.consumer.InscripcionFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.LegajoFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.PreInscripcionFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.model.InscripcionFacultad;
import ar.edu.um.tesoreria.rest.extern.model.LegajoFacultad;
import ar.edu.um.tesoreria.rest.extern.model.PreInscripcionFacultad;
import ar.edu.um.tesoreria.rest.model.CarreraChequera;
import ar.edu.um.tesoreria.rest.model.ChequeraSerie;
import ar.edu.um.tesoreria.rest.model.Facultad;
import ar.edu.um.tesoreria.rest.model.kotlin.Persona;
import ar.edu.um.tesoreria.rest.model.dto.DeudaChequera;
import ar.edu.um.tesoreria.rest.model.dto.DeudaPersona;
import ar.edu.um.tesoreria.rest.model.view.PersonaKey;
import ar.edu.um.tesoreria.rest.repository.IPersonaRepository;
import ar.edu.um.tesoreria.rest.service.view.PersonaKeyService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class PersonaService {

	@Autowired
	private IPersonaRepository repository;

	@Autowired
	private PersonaKeyService personaKeyService;

	@Autowired
	private InscripcionFacultadConsumer inscripcionFacultadConsumer;

	@Autowired
	private PreInscripcionFacultadConsumer preInscripcionFacultadConsumer;

	@Autowired
	private FacultadService facultadService;

	@Autowired
	private ChequeraSerieService chequeraSerieService;

	@Autowired
	private CarreraChequeraService carreraChequeraService;

	@Autowired
	private LegajoFacultadConsumer legajoFacultadConsumer;

	@Autowired
	private ChequeraCuotaService chequeraCuotaService;

	public Persona findByUnique(BigDecimal personaId, Integer documentoId) {
		return repository.findByPersonaIdAndDocumentoId(personaId, documentoId)
				.orElseThrow(() -> new PersonaException(personaId, documentoId));
	}

	public Persona findByPersonaId(BigDecimal personaId) {
		return repository.findTopByPersonaId(personaId).orElseThrow(() -> new PersonaException(personaId));
	}

	public List<Persona> findAllSantander() {
		return repository.findAllByCbuLike("072%");
	}

	public DeudaPersona deudaByPersona(BigDecimal personaId, Integer documentoId) {
		DeudaPersona deudaPersona = new DeudaPersona(personaId, documentoId, 0, BigDecimal.ZERO, new ArrayList<>());
		for (ChequeraSerie chequera : chequeraSerieService.findAllByPersonaIdAndDocumentoId(personaId, documentoId,
				null)) {
			DeudaChequera deuda = chequeraCuotaService.calculateDeuda(chequera.getFacultadId(),
					chequera.getTipoChequeraId(), chequera.getChequeraSerieId());
			if (deuda.getCuotas() > 0) {
				deudaPersona.getDeudas().add(deuda);
				deudaPersona.setCuotas(deudaPersona.getCuotas() + deuda.getCuotas());
				deudaPersona.setDeuda(deudaPersona.getDeuda().add(deuda.getDeuda()).setScale(2, RoundingMode.HALF_UP));
			}
		}
		return deudaPersona;
	}

	public DeudaPersona deudaByPersonaExtended(BigDecimal personaId, Integer documentoId) {
		DeudaPersona deudaPersona = new DeudaPersona(personaId, documentoId, 0, BigDecimal.ZERO, new ArrayList<>());
		for (ChequeraSerie chequera : chequeraSerieService.findAllByPersonaIdAndDocumentoId(personaId, documentoId,
				null)) {
			DeudaChequera deuda = chequeraCuotaService.calculateDeudaExtended(chequera.getFacultadId(),
					chequera.getTipoChequeraId(), chequera.getChequeraSerieId());
			if (deuda.getCuotas() > 0) {
				deudaPersona.getDeudas().add(deuda);
				deudaPersona.setCuotas(deudaPersona.getCuotas() + deuda.getCuotas());
				deudaPersona.setDeuda(deudaPersona.getDeuda().add(deuda.getDeuda()).setScale(2, RoundingMode.HALF_UP));
			}
		}
		return deudaPersona;
	}

	public List<PersonaKey> findAllInscriptosSinChequera(Integer facultadId, Integer lectivoId, Integer geograficaId,
			Integer curso) {
		Facultad facultad = facultadService.findByFacultadId(facultadId);
		Map<String, InscripcionFacultad> inscriptos = inscripcionFacultadConsumer
				.findAllByCursoSinProvisoria(facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId,
						geograficaId, curso)
				.stream().collect(Collectors.toMap(InscripcionFacultad::getPersonaKey, Function.identity(),
						(inscripto, replacement) -> inscripto));
		// Elimina los que ya tengan chequera
		Map<String, InscripcionFacultad> pendientes = new HashMap<String, InscripcionFacultad>();
		for (InscripcionFacultad inscripto : inscriptos.values()) {
			Boolean add = true;
			try {
				if (facultadId == 15) {
					chequeraSerieService.findByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
							inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId, lectivoId, geograficaId);
				} else {
					chequeraSerieService.findGradoByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
							inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId, lectivoId, geograficaId);
				}
				add = false;
			} catch (ChequeraSerieException e) {
				log.debug("Sin chequera");
			}
			// Verifica los alumnos de intercambio
			if (add) {
				try {
					LegajoFacultad legajo = legajoFacultadConsumer.findByPersona(facultad.getApiserver(),
							facultad.getApiport(), inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId);
					if (legajo.getIntercambio() == 1) {
						add = false;
					}
				} catch (Exception e) {
					log.debug("Sin legajo");
				}
			}
			// Agrega el alumno
			if (add) {
				pendientes.put(inscripto.getPersonaKey(), inscripto);
			}
		}
		return personaKeyService.findAllByUnifiedIn(new ArrayList<>(pendientes.keySet()),
				Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
	}

	public List<PersonaKey> findAllInscriptosSinChequeraDefault(Integer facultadId, Integer lectivoId,
			Integer geograficaId, Integer claseChequeraId, Integer curso) {
		Facultad facultad = facultadService.findByFacultadId(facultadId);
		Map<String, InscripcionFacultad> inscriptos = inscripcionFacultadConsumer
				.findAllByCursoSinProvisoria(facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId,
						geograficaId, curso)
				.stream().collect(Collectors.toMap(InscripcionFacultad::getPersonaKey, Function.identity(),
						(inscripto, replacemente) -> inscripto));
		Map<String, CarreraChequera> tipos = carreraChequeraService
				.findAllByFacultadIdAndLectivoIdAndGeograficaIdAndClaseChequeraIdAndCurso(facultadId, lectivoId,
						geograficaId, claseChequeraId, curso)
				.stream().collect(Collectors.toMap(CarreraChequera::getCarreraKey, Function.identity(),
						(tipo, replacement) -> tipo));
		// Elimina los que ya tengan chequera
		Map<String, InscripcionFacultad> pendientes = new HashMap<String, InscripcionFacultad>();
		for (InscripcionFacultad inscripto : inscriptos.values()) {
			CarreraChequera carreraChequera = null;
			if (!tipos.containsKey(facultadId + "." + inscripto.getPlanId() + "." + inscripto.getCarreraId()))
				continue;
			carreraChequera = tipos.get(facultadId + "." + inscripto.getPlanId() + "." + inscripto.getCarreraId());
			Boolean add = true;
			try {
				chequeraSerieService
						.findByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(
								inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId, lectivoId,
								geograficaId, carreraChequera.getTipoChequeraId());
				add = false;
			} catch (ChequeraSerieException e) {
				log.debug("Sin chequera");
			}
			// Verifica los alumnos de intercambio
			if (add) {
				try {
					LegajoFacultad legajo = legajoFacultadConsumer.findByPersona(facultad.getApiserver(),
							facultad.getApiport(), inscripto.getPersonaId(), inscripto.getDocumentoId(), facultadId);
					if (legajo.getIntercambio() == 1) {
						add = false;
					}
				} catch (Exception e) {
					log.debug("Sin legajo");
				}
			}
			// Agrega el alumno
			if (add) {
				pendientes.put(inscripto.getPersonaKey(), inscripto);
			}
		}
		return personaKeyService.findAllByUnifiedIn(new ArrayList<>(pendientes.keySet()),
				Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
	}

	public List<PersonaKey> findAllPreInscriptosSinChequera(Integer facultadId, Integer lectivoId,
			Integer geograficaId) {
		Facultad facultad = facultadService.findByFacultadId(facultadId);
		Map<String, PreInscripcionFacultad> preinscriptos = preInscripcionFacultadConsumer
				.findAllByPreInscriptos(facultad.getApiserver(), facultad.getApiport(), facultadId, lectivoId,
						geograficaId)
				.stream().collect(Collectors.toMap(PreInscripcionFacultad::getPersonaKey, Function.identity(),
						(preinscripto, replacement) -> preinscripto));
		// Elimina los que ya tengan chequera
		Map<String, PreInscripcionFacultad> pendientes = new HashMap<String, PreInscripcionFacultad>();
		for (PreInscripcionFacultad preinscripto : preinscriptos.values()) {
			Boolean add = true;
			try {
				chequeraSerieService
						.findPreuniversitarioByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(
								preinscripto.getPersonaId(), preinscripto.getDocumentoId(), facultadId, lectivoId - 1,
								geograficaId);
				add = false;
			} catch (ChequeraSerieException e) {
				log.debug("Sin chequera");
			}
			// Agrega el alumno
			if (add) {
				pendientes.put(preinscripto.getPersonaKey(), preinscripto);
			}
		}
		return personaKeyService.findAllByUnifiedIn(new ArrayList<>(pendientes.keySet()),
				Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
	}

	public List<PersonaKey> findAllDeudorByLectivoId(Integer facultadId, Integer lectivoId, Integer geograficaId,
			Integer cuotas) {
		List<String> unifieds = new ArrayList<String>();
		for (ChequeraSerie serie : chequeraSerieService.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId,
				facultadId, geograficaId)) {
			if (cuotas <= chequeraCuotaService.findAllDebidas(facultadId, serie.getTipoChequeraId(),
					serie.getChequeraSerieId(), serie.getAlternativaId()).size()) {
				unifieds.add(serie.getPersonaKey());
			}
		}
		return personaKeyService.findAllByUnifiedIn(unifieds,
				Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
	}

	public List<PersonaKey> findByUnifieds(List<String> unifieds) {
		return personaKeyService.findAllByUnifiedIn(unifieds, Sort.by("apellido").ascending());
	}

	public List<PersonaKey> findByStrings(List<String> conditions) {
		return personaKeyService.findAllByStrings(conditions);
	}

	public Persona findByUniqueId(Long uniqueId) {
		return repository.findByUniqueId(uniqueId).orElseThrow(() -> new PersonaException(uniqueId));
	}

	public Persona add(Persona persona) {
		repository.save(persona);
		return persona;
	}

	public Persona update(Persona newpersona, Long uniqueId) {
		return repository.findByUniqueId(uniqueId).map(persona -> {
			persona = new Persona(uniqueId, newpersona.getPersonaId(), newpersona.getDocumentoId(),
					newpersona.getApellido(), newpersona.getNombre(), newpersona.getSexo(), newpersona.getPrimero(),
					newpersona.getCuit(), newpersona.getCbu(), newpersona.getPassword());
			repository.save(persona);
			return persona;
		}).orElseThrow(() -> new PersonaException(uniqueId));
	}

}
