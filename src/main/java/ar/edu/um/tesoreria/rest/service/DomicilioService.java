/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.DomicilioNotFoundException;
import ar.edu.um.tesoreria.rest.exception.LocalidadNotFoundException;
import ar.edu.um.tesoreria.rest.exception.ProvinciaNotFoundException;
import ar.edu.um.tesoreria.rest.extern.consumer.DomicilioFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.LocalidadFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.PersonaFacultadConsumer;
import ar.edu.um.tesoreria.rest.extern.consumer.ProvinciaFacultadConsumer;
import ar.edu.um.tesoreria.rest.model.Domicilio;
import ar.edu.um.tesoreria.rest.model.Facultad;
import ar.edu.um.tesoreria.rest.model.Localidad;
import ar.edu.um.tesoreria.rest.model.Persona;
import ar.edu.um.tesoreria.rest.model.Provincia;
import ar.edu.um.tesoreria.rest.model.view.DomicilioKey;
import ar.edu.um.tesoreria.rest.repository.IDomicilioRepository;
import ar.edu.um.tesoreria.rest.service.view.DomicilioKeyService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class DomicilioService {

	@Autowired
	private IDomicilioRepository repository;

	@Autowired
	private DomicilioKeyService domicilioKeyService;

	@Autowired
	private FacultadService facultadService;

	@Autowired
	private PersonaFacultadConsumer personaFacultadConsumer;

	@Autowired
	private DomicilioFacultadConsumer domicilioFacultadConsumer;

	@Autowired
	private ProvinciaService provinciaService;

	@Autowired
	private ProvinciaFacultadConsumer provinciaFacultadConsumer;

	@Autowired
	private LocalidadService localidadService;

	@Autowired
	private LocalidadFacultadConsumer localidadFacultadConsumer;

	public List<DomicilioKey> findAllByUnifieds(List<String> unifieds) {
		return domicilioKeyService.findAllByUnifiedIn(unifieds);
	}

	public Domicilio findByDomicilioId(Long domicilioId) {
		return repository.findByDomicilioId(domicilioId).orElseThrow(() -> new DomicilioNotFoundException(domicilioId));
	}

	public Domicilio findByUnique(BigDecimal personaId, Integer documentoId) {
		return repository.findByPersonaIdAndDocumentoId(personaId, documentoId)
				.orElseThrow(() -> new DomicilioNotFoundException(personaId, documentoId));
	}

	public Domicilio findFirstByPersonaId(BigDecimal personaId) {
		return repository.findFirstByPersonaId(personaId).orElseThrow(() -> new DomicilioNotFoundException(personaId));
	}

	@Transactional
	public Domicilio add(Domicilio domicilio, Boolean sincronize) {
		domicilio.setFecha(OffsetDateTime.now());
		repository.save(domicilio);

		if (sincronize)
			this.sincronizeFacultad(domicilio);

		return domicilio;
	}

	@Transactional
	public Domicilio update(Domicilio newDomicilio, Long domicilioId, Boolean sincronize) {
		return repository.findByDomicilioId(domicilioId).map(domicilio -> {
			domicilio = new Domicilio(domicilioId, newDomicilio.getPersonaId(), newDomicilio.getDocumentoId(),
					OffsetDateTime.now(), newDomicilio.getCalle(), newDomicilio.getPuerta(), newDomicilio.getPiso(),
					newDomicilio.getDpto(), newDomicilio.getTelefono(), newDomicilio.getMovil(),
					newDomicilio.getObservaciones(), newDomicilio.getCodigoPostal(), newDomicilio.getFacultadId(),
					newDomicilio.getProvinciaId(), newDomicilio.getLocalidadId(), newDomicilio.getEmailPersonal(),
					newDomicilio.getEmailInstitucional(), newDomicilio.getLaboral());
			repository.save(domicilio);

			if (sincronize)
				this.sincronizeFacultad(domicilio);

			return domicilio;
		}).orElseThrow(() -> new DomicilioNotFoundException(domicilioId));
	}

	private void sincronizeFacultad(Domicilio domicilio) {
		for (Facultad facultad : facultadService.findFacultades()) {
			if (!facultad.getApiserver().equals("")) {
				Persona persona = personaFacultadConsumer.findByUnique(facultad.getApiserver(), facultad.getApiport(),
						domicilio.getPersonaId(), domicilio.getDocumentoId());
				if (persona.getUniqueId() != null) {
					domicilioFacultadConsumer.sincronize(facultad.getApiserver(), facultad.getApiport(), domicilio);
				}
			}
		}
	}

	public Domicilio sincronize(Domicilio domicilio) {
		Domicilio otro_domicilio = repository
				.findByPersonaIdAndDocumentoId(domicilio.getPersonaId(), domicilio.getDocumentoId())
				.orElse(new Domicilio());
		if (otro_domicilio.getDomicilioId() == null)
			otro_domicilio = this.add(domicilio, false);
		else
			otro_domicilio = this.update(domicilio, otro_domicilio.getDomicilioId(), false);
		return otro_domicilio;
	}

	@Transactional
	public Integer capture(BigDecimal personaId, Integer documentoId) {
		for (Facultad facultad : facultadService.findFacultades()) {
			if (!facultad.getApiserver().equals("")) {
				Domicilio domicilio = null;
				if (facultad.getFacultadId() != 15) {
					domicilio = domicilioFacultadConsumer.findByUnique(facultad.getApiserver(), facultad.getApiport(),
							personaId, documentoId);
					log.debug("Domicilio -> {}", domicilio);
				} else {
					domicilio = domicilioFacultadConsumer.findPagadorByUnique(facultad.getApiserver(),
							facultad.getApiport(), personaId, documentoId);
					domicilio.setEmailInstitucional("");
					log.debug("Domicilio ETEC -> {}", domicilio);
				}
				domicilio.setPersonaId(personaId);
				domicilio.setDocumentoId(documentoId);
				if (domicilio.getDomicilioId() != null) {
					domicilio.setDomicilioId(null);
					Domicilio domicilio_old = repository.findByPersonaIdAndDocumentoId(personaId, documentoId)
							.orElse(new Domicilio());
					if (domicilio_old.getDomicilioId() != null) {
						domicilio.setDomicilioId(domicilio_old.getDomicilioId());
						domicilio.setPersonaId(domicilio_old.getPersonaId());
						domicilio.setDocumentoId(domicilio_old.getDocumentoId());
						if (domicilio.getProvinciaId() == 0)
							domicilio.setProvinciaId(null);
						if (domicilio.getLocalidadId() == 0)
							domicilio.setLocalidadId(null);
					}
					domicilio.setFacultadId(facultad.getFacultadId());
					if (domicilio.getProvinciaId() == null || domicilio.getProvinciaId() == 0)
						domicilio.setProvinciaId(1);
					if (domicilio.getLocalidadId() == null || domicilio.getLocalidadId() == 0) {
						domicilio.setProvinciaId(1);
						domicilio.setLocalidadId(1);
					}
					if (domicilio.getProvinciaId() != null) {
						Provincia provincia_old = null;
						try {
							provincia_old = provinciaService.findByUnique(domicilio.getFacultadId(),
									domicilio.getProvinciaId());
						} catch (ProvinciaNotFoundException e) {
							provincia_old = new Provincia();
						}
						if (provincia_old.getUniqueId() == null) {
							// Capturar provincia
							Provincia provincia = provinciaFacultadConsumer.findByUnique(facultad.getApiserver(),
									facultad.getApiport(), domicilio.getFacultadId(), domicilio.getProvinciaId());
							provincia.setUniqueId(null);
							provinciaService.add(provincia);
						}
						Localidad localidad_old = null;
						try {
							localidad_old = localidadService.findByUnique(domicilio.getFacultadId(),
									domicilio.getProvinciaId(), domicilio.getLocalidadId());
						} catch (LocalidadNotFoundException e) {
							localidad_old = new Localidad();
						}
						if (localidad_old.getUniqueId() == null) {
							// Capturar localidad
							Localidad localidad = localidadFacultadConsumer.findByUnique(facultad.getApiserver(),
									facultad.getApiport(), domicilio.getFacultadId(), domicilio.getProvinciaId(),
									domicilio.getLocalidadId());
							localidad.setUniqueId(null);
							localidadService.add(localidad);
						}
					}
					domicilio = repository.save(domicilio);
					log.debug("Domicilio -> {}", domicilio);
					return facultad.getFacultadId();
				}
			}
		}
		return 0;
	}

}
