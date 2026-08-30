package um.tesoreria.core.hexagonal.personas.domicilio.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.in.CaptureDomicilioUseCase;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.out.DomicilioRepository;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.extern.consumer.DomicilioFacultadConsumer;
import um.tesoreria.core.extern.consumer.LocalidadFacultadConsumer;
import um.tesoreria.core.extern.consumer.ProvinciaFacultadConsumer;
import um.tesoreria.core.exception.LocalidadException;
import um.tesoreria.core.exception.ProvinciaException;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.model.Localidad;
import um.tesoreria.core.model.Provincia;
import um.tesoreria.core.service.LocalidadService;
import um.tesoreria.core.service.ProvinciaService;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class CaptureDomicilioUseCaseImpl implements CaptureDomicilioUseCase {

    private static final Integer DEFAULT_PROVINCIA_ID = 1;
    private static final Integer DEFAULT_LOCALIDAD_ID = 1;

    private final DomicilioRepository repository;
    private final FacultadService facultadService;
    private final DomicilioFacultadConsumer domicilioFacultadConsumer;
    private final ProvinciaService provinciaService;
    private final ProvinciaFacultadConsumer provinciaFacultadConsumer;
    private final LocalidadService localidadService;
    private final LocalidadFacultadConsumer localidadFacultadConsumer;

    @Override
    public Integer capture(BigDecimal personaId, Integer documentoId) {
        log.info("CAPTURE[usecase] -> inicio personaId={}, documentoId={}", personaId, documentoId);
        for (Facultad facultad : facultadService.findFacultades()) {
            log.info("CAPTURE[usecase] -> facultadId={}, apiserver={}", facultad.getFacultadId(), facultad.getApiserver());
            if (facultad.getApiserver() == null || facultad.getApiserver().isEmpty()) {
                log.warn("CAPTURE[usecase] -> facultadId={} sin apiserver, se omite", facultad.getFacultadId());
                continue;
            }

            Domicilio domicilio = null;
            try {
                var entity = domicilioFacultadConsumer.findByUnique(
                        facultad.getFacultadId(),
                        personaId, documentoId);
                log.info("CAPTURE[usecase] -> dato EXTERNO leído (facultadId={}): {}",
                        facultad.getFacultadId(),
                        entity == null ? "NULL (body vacío)" : entity.jsonify());
                domicilio = Domicilio.builder()
                        .domicilioId(null)
                        .personaId(entity.getPersonaId())
                        .documentoId(entity.getDocumentoId())
                        .fecha(entity.getFecha())
                        .calle(entity.getCalle())
                        .puerta(entity.getPuerta())
                        .piso(entity.getPiso())
                        .dpto(entity.getDpto())
                        .telefono(entity.getTelefono())
                        .movil(entity.getMovil())
                        .observaciones(entity.getObservaciones())
                        .codigoPostal(entity.getCodigoPostal())
                        .facultadId(entity.getFacultadId())
                        .provinciaId(entity.getProvinciaId())
                        .localidadId(entity.getLocalidadId())
                        .emailPersonal(entity.getEmailPersonal())
                        .emailInstitucional(entity.getEmailInstitucional())
                        .laboral(entity.getLaboral())
                        .build();
                log.info("CAPTURE[usecase] -> EXTERNO mapeado a dominio: {}", domicilio.jsonify());
            } catch (Exception e) {
                log.error("CAPTURE[usecase] -> FALLO lectura externa facultadId={}, personaId={}, documentoId={}",
                        facultad.getFacultadId(), personaId, documentoId, e);
                continue;
            }

            if (!esCapturable(domicilio)) {
                log.warn("CAPTURE[usecase] -> facultadId={} devolvió datos sin correos (vacíos), se continúa con la siguiente facultad",
                        facultad.getFacultadId());
                continue;
            }

            Domicilio finalDomicilio = domicilio;
            repository.findByUnique(personaId, documentoId).ifPresentOrElse(existing -> {
                log.info("CAPTURE[usecase] -> LOCAL EXISTENTE (domicilioId={}) se actualizará con el dato externo: {}",
                        existing.getDomicilioId(), existing.jsonify());
                finalDomicilio.setDomicilioId(existing.getDomicilioId());
                finalDomicilio.setPersonaId(existing.getPersonaId());
                finalDomicilio.setDocumentoId(existing.getDocumentoId());
            }, () -> log.info("CAPTURE[usecase] -> sin registro local, se dará de ALTA"));

            if (domicilio.getProvinciaId() == null || domicilio.getProvinciaId() == 0) {
                log.warn("CAPTURE[usecase] -> provinciaId vacío en dato externo, se aplica default {}", DEFAULT_PROVINCIA_ID);
                domicilio.setProvinciaId(DEFAULT_PROVINCIA_ID);
            }
            if (domicilio.getLocalidadId() == null || domicilio.getLocalidadId() == 0) {
                log.warn("CAPTURE[usecase] -> localidadId vacío en dato externo, se aplican defaults provinciaId={}, localidadId={}",
                        DEFAULT_PROVINCIA_ID, DEFAULT_LOCALIDAD_ID);
                domicilio.setProvinciaId(DEFAULT_PROVINCIA_ID);
                domicilio.setLocalidadId(DEFAULT_LOCALIDAD_ID);
            }

            domicilio.setFacultadId(facultad.getFacultadId());
            log.info("CAPTURE[usecase] -> a persistir ({}): {}",
                    domicilio.getDomicilioId() == null ? "INSERT" : "MERGE/UPDATE",
                    domicilio.jsonify());
            sincronizeProvinciaAndLocalidad(domicilio);

            repository.create(domicilio);
            log.info("CAPTURE[usecase] -> capturado por facultadId={}", facultad.getFacultadId());
            return facultad.getFacultadId();
        }
        log.warn("CAPTURE[usecase] -> ninguna facultad devolvió datos capturables (con correos), retorno 0");
        return 0;
    }

    private boolean esCapturable(Domicilio domicilio) {
        boolean conEmailPersonal = domicilio.getEmailPersonal() != null && !domicilio.getEmailPersonal().isBlank();
        boolean conEmailInstitucional = domicilio.getEmailInstitucional() != null && !domicilio.getEmailInstitucional().isBlank();
        return conEmailPersonal || conEmailInstitucional;
    }

    private void sincronizeProvinciaAndLocalidad(Domicilio domicilio) {
        if (domicilio.getProvinciaId() == null) {
            log.warn("CAPTURE[usecase] -> sincronizeProvinciaAndLocalidad omitido: provinciaId null");
            return;
        }
        try {
            provinciaService.findByUnique(domicilio.getFacultadId(), domicilio.getProvinciaId());
            log.info("CAPTURE[usecase] -> provincia local OK (facultadId={}, provinciaId={})",
                    domicilio.getFacultadId(), domicilio.getProvinciaId());
        } catch (ProvinciaException e) {
            log.warn("CAPTURE[usecase] -> provincia no existe local, se lee EXTERNA (facultadId={}, provinciaId={})",
                    domicilio.getFacultadId(), domicilio.getProvinciaId());
            Provincia provincia = provinciaFacultadConsumer.findByUnique(
                    domicilio.getFacultadId(), domicilio.getProvinciaId());
            log.info("CAPTURE[usecase] -> provincia externa leída: {}", provincia);
            provincia.setUniqueId(null);
            provinciaService.add(provincia);
        }
        try {
            localidadService.findByUnique(domicilio.getFacultadId(),
                    domicilio.getProvinciaId(), domicilio.getLocalidadId());
            log.info("CAPTURE[usecase] -> localidad local OK (facultadId={}, provinciaId={}, localidadId={})",
                    domicilio.getFacultadId(), domicilio.getProvinciaId(), domicilio.getLocalidadId());
        } catch (LocalidadException e) {
            log.warn("CAPTURE[usecase] -> localidad no existe local, se lee EXTERNA (facultadId={}, provinciaId={}, localidadId={})",
                    domicilio.getFacultadId(), domicilio.getProvinciaId(), domicilio.getLocalidadId());
            Localidad localidad = localidadFacultadConsumer.findByUnique(
                    domicilio.getFacultadId(), domicilio.getProvinciaId(),
                    domicilio.getLocalidadId());
            log.info("CAPTURE[usecase] -> localidad externa leída: {}", localidad);
            localidad.setUniqueId(null);
            localidadService.add(localidad);
        }
    }
}
