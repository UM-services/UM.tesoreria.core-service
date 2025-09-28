/**
 *
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.exception.DomicilioException;
import um.tesoreria.core.exception.LocalidadException;
import um.tesoreria.core.exception.ProvinciaException;
import um.tesoreria.core.extern.consumer.*;
import um.tesoreria.core.kotlin.model.Domicilio;
import um.tesoreria.core.kotlin.model.Facultad;
import um.tesoreria.core.model.Localidad;
import um.tesoreria.core.model.Provincia;
import um.tesoreria.core.model.view.DomicilioKey;
import um.tesoreria.core.repository.DomicilioRepository;
import um.tesoreria.core.service.view.DomicilioKeyService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.Persona;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DomicilioService {

    private final DomicilioRepository repository;
    private final DomicilioKeyService domicilioKeyService;
    private final FacultadService facultadService;
    private final PersonaFacultadConsumer personaFacultadConsumer;
    private final DomicilioFacultadConsumer domicilioFacultadConsumer;
    private final ProvinciaService provinciaService;
    private final ProvinciaFacultadConsumer provinciaFacultadConsumer;
    private final LocalidadService localidadService;
    private final LocalidadFacultadConsumer localidadFacultadConsumer;
    private final InscripcionFacultadConsumer inscripcionFacultadConsumer;

    public List<DomicilioKey> findAllByUnifieds(List<String> unifieds) {
        return domicilioKeyService.findAllByUnifiedIn(unifieds);
    }

    public Domicilio findByDomicilioId(Long domicilioId) {
        return repository.findByDomicilioId(domicilioId).orElseThrow(() -> new DomicilioException(domicilioId));
    }

    public Domicilio findByUnique(BigDecimal personaId, Integer documentoId) {
        var domicilio = repository.findByPersonaIdAndDocumentoId(personaId, documentoId)
                .orElseThrow(() -> new DomicilioException(personaId, documentoId));
        log.debug("Domicilio -> {}", domicilio.jsonify());
		return domicilio;
    }

    public Domicilio findFirstByPersonaId(BigDecimal personaId) {
        return repository.findFirstByPersonaId(personaId).orElseThrow(() -> new DomicilioException(personaId));
    }

    @Transactional
    public Domicilio add(Domicilio domicilio, Boolean sincronize) {
        domicilio.setFecha(OffsetDateTime.now());
        domicilio = repository.save(domicilio);

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
                    newDomicilio.getEmailInstitucional(), newDomicilio.getLaboral(), "");
            log.debug("Domicilio Previo -> {}", domicilio.jsonify());
			domicilio = repository.save(domicilio);
            log.debug("Domicilio Post -> {}", domicilio.jsonify());

            if (sincronize)
                this.sincronizeFacultad(domicilio);

            return domicilio;
        }).orElseThrow(() -> new DomicilioException(domicilioId));
    }

    private void sincronizeFacultad(Domicilio domicilio) {
        for (Facultad facultad : facultadService.findFacultades()) {
            if (!facultad.getApiserver().isEmpty()) {
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
            if (!facultad.getApiserver().isEmpty()) {
                Domicilio domicilio = null;
                domicilio = domicilioFacultadConsumer.findByUnique(facultad.getApiserver(), facultad.getApiport(),
                        personaId, documentoId);
                log.debug("Domicilio -> {}", domicilio.jsonify());
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
                        } catch (ProvinciaException e) {
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
                        } catch (LocalidadException e) {
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
                    log.debug("Domicilio Previo -> {}", domicilio.jsonify());
                    domicilio = repository.save(domicilio);
                    log.debug("Domicilio Post -> {}", domicilio.jsonify());
                    return facultad.getFacultadId();
                }
            }
        }
        return 0;
    }

    public Domicilio findWithPagador(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        var facultad = facultadService.findByFacultadId(facultadId);
        var inscripcionFull = inscripcionFacultadConsumer.findInscripcionFull(facultad.getApiserver(), facultad.getApiport(), facultad.getFacultadId(), personaId, documentoId, lectivoId);
        Domicilio domicilio = repository.findByPersonaIdAndDocumentoId(personaId, documentoId)
                .orElseThrow(() -> new DomicilioException(personaId, documentoId));
        if (inscripcionFull.getDomicilioPago() != null) {
            var domicilioPago = inscripcionFull.getDomicilioPago();
            if (!domicilioPago.getEmailPersonal().isEmpty()) {
                domicilio.setEmailPagador(domicilioPago.getEmailPersonal());
            }
        }
        return domicilio;
    }

}
