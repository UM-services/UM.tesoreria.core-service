/**
 *
 */
package um.tesoreria.core.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.DomicilioException;
import um.tesoreria.core.exception.LocalidadException;
import um.tesoreria.core.exception.ProvinciaException;
import um.tesoreria.core.extern.consumer.*;
import um.tesoreria.core.kotlin.model.Domicilio;
import um.tesoreria.core.kotlin.model.Facultad;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;
import um.tesoreria.core.model.Localidad;
import um.tesoreria.core.model.Provincia;
import um.tesoreria.core.model.view.DomicilioKey;
import um.tesoreria.core.repository.DomicilioRepository;
import um.tesoreria.core.service.view.DomicilioKeyService;

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

    private static final Integer DEFAULT_PROVINCIA_ID = 1;
    private static final Integer DEFAULT_LOCALIDAD_ID = 1;

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
        return repository.findByPersonaIdAndDocumentoId(personaId, documentoId)
                .orElseThrow(() -> new DomicilioException(personaId, documentoId));
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
            domicilio.setPersonaId(newDomicilio.getPersonaId());
            domicilio.setDocumentoId(newDomicilio.getDocumentoId());
            domicilio.setFecha(OffsetDateTime.now());
            domicilio.setCalle(newDomicilio.getCalle());
            domicilio.setPuerta(newDomicilio.getPuerta());
            domicilio.setPiso(newDomicilio.getPiso());
            domicilio.setDpto(newDomicilio.getDpto());
            domicilio.setTelefono(newDomicilio.getTelefono());
            domicilio.setMovil(newDomicilio.getMovil());
            domicilio.setObservaciones(newDomicilio.getObservaciones());
            domicilio.setCodigoPostal(newDomicilio.getCodigoPostal());
            domicilio.setFacultadId(newDomicilio.getFacultadId());
            domicilio.setProvinciaId(newDomicilio.getProvinciaId());
            domicilio.setLocalidadId(newDomicilio.getLocalidadId());
            domicilio.setEmailPersonal(newDomicilio.getEmailPersonal());
            domicilio.setEmailInstitucional(newDomicilio.getEmailInstitucional());
            domicilio.setLaboral(newDomicilio.getLaboral());
            domicilio.setEmailPagador("");
            domicilio = repository.save(domicilio);

            if (sincronize)
                this.sincronizeFacultad(domicilio);

            return domicilio;
        }).orElseThrow(() -> new DomicilioException(domicilioId));
    }

    private void sincronizeFacultad(Domicilio domicilio) {
        for (Facultad facultad : facultadService.findFacultades()) {
            if (!facultad.getApiserver().isEmpty()) {
                PersonaEntity personaEntity = personaFacultadConsumer.findByUnique(facultad.getApiserver(), facultad.getApiport(),
                        domicilio.getPersonaId(), domicilio.getDocumentoId());
                if (personaEntity.getUniqueId() != null) {
                    domicilioFacultadConsumer.sincronize(facultad.getApiserver(), facultad.getApiport(), domicilio);
                }
            }
        }
    }

    public Domicilio sincronize(Domicilio domicilio) {
        return repository.findByPersonaIdAndDocumentoId(domicilio.getPersonaId(), domicilio.getDocumentoId())
                .map(existingDomicilio -> this.update(domicilio, existingDomicilio.getDomicilioId(), false))
                .orElseGet(() -> this.add(domicilio, false));
    }

    @Transactional
    public Integer capture(BigDecimal personaId, Integer documentoId) {
        for (Facultad facultad : facultadService.findFacultades()) {
            if (facultad.getApiserver().isEmpty()) {
                continue;
            }

            Domicilio domicilio = domicilioFacultadConsumer.findByUnique(facultad.getApiserver(), facultad.getApiport(),
                    personaId, documentoId);

            if (domicilio.getDomicilioId() != null) {
                domicilio.setPersonaId(personaId);
                domicilio.setDocumentoId(documentoId);
                domicilio.setDomicilioId(null);

                repository.findByPersonaIdAndDocumentoId(personaId, documentoId)
                        .ifPresent(existingDomicilio -> {
                            domicilio.setDomicilioId(existingDomicilio.getDomicilioId());
                            domicilio.setPersonaId(existingDomicilio.getPersonaId());
                            domicilio.setDocumentoId(existingDomicilio.getDocumentoId());
                        });

                if (domicilio.getProvinciaId() == null || domicilio.getProvinciaId() == 0) {
                    domicilio.setProvinciaId(DEFAULT_PROVINCIA_ID);
                }
                if (domicilio.getLocalidadId() == null || domicilio.getLocalidadId() == 0) {
                    domicilio.setProvinciaId(DEFAULT_PROVINCIA_ID);
                    domicilio.setLocalidadId(DEFAULT_LOCALIDAD_ID);
                }

                domicilio.setFacultadId(facultad.getFacultadId());
                sincronizeProvinciaAndLocalidad(domicilio, facultad);

                repository.save(domicilio);
                return facultad.getFacultadId();
            }
        }
        return 0;
    }

    private void sincronizeProvinciaAndLocalidad(Domicilio domicilio, Facultad facultad) {
        if (domicilio.getProvinciaId() == null) {
            return;
        }
        // Sincronizar Provincia
        try {
            provinciaService.findByUnique(domicilio.getFacultadId(), domicilio.getProvinciaId());
        } catch (ProvinciaException e) {
            // No existe, la capturamos
            Provincia provincia = provinciaFacultadConsumer.findByUnique(facultad.getApiserver(),
                    facultad.getApiport(), domicilio.getFacultadId(), domicilio.getProvinciaId());
            provincia.setUniqueId(null);
            provinciaService.add(provincia);
        }

        // Sincronizar Localidad
        try {
            localidadService.findByUnique(domicilio.getFacultadId(),
                    domicilio.getProvinciaId(), domicilio.getLocalidadId());
        } catch (LocalidadException e) {
            // No existe, la capturamos
            Localidad localidad = localidadFacultadConsumer.findByUnique(facultad.getApiserver(),
                    facultad.getApiport(), domicilio.getFacultadId(), domicilio.getProvinciaId(),
                    domicilio.getLocalidadId());
            localidad.setUniqueId(null);
            localidadService.add(localidad);
        }
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
