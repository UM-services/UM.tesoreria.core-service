package um.tesoreria.core.hexagonal.domicilio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.in.CaptureDomicilioUseCase;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.out.DomicilioRepository;
import um.tesoreria.core.hexagonal.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.extern.consumer.DomicilioFacultadConsumer;
import um.tesoreria.core.extern.consumer.LocalidadFacultadConsumer;
import um.tesoreria.core.extern.consumer.ProvinciaFacultadConsumer;
import um.tesoreria.core.exception.LocalidadException;
import um.tesoreria.core.exception.ProvinciaException;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.model.Localidad;
import um.tesoreria.core.model.Provincia;
import um.tesoreria.core.service.LocalidadService;
import um.tesoreria.core.service.ProvinciaService;

import java.math.BigDecimal;

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
        for (Facultad facultad : facultadService.findFacultades()) {
            if (facultad.getApiserver().isEmpty()) {
                continue;
            }

            Domicilio domicilio = null;
            try {
                var entity = domicilioFacultadConsumer.findByUnique(
                        facultad.getApiserver(), facultad.getApiport(),
                        personaId, documentoId);
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
            } catch (Exception e) {
                continue;
            }

            if (domicilio != null) {
                Domicilio finalDomicilio = domicilio;
                repository.findByUnique(personaId, documentoId).ifPresent(existing -> {
                    finalDomicilio.setDomicilioId(existing.getDomicilioId());
                    finalDomicilio.setPersonaId(existing.getPersonaId());
                    finalDomicilio.setDocumentoId(existing.getDocumentoId());
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

                repository.create(domicilio);
                return facultad.getFacultadId();
            }
        }
        return 0;
    }

    private void sincronizeProvinciaAndLocalidad(Domicilio domicilio, Facultad facultad) {
        if (domicilio.getProvinciaId() == null) {
            return;
        }
        try {
            provinciaService.findByUnique(domicilio.getFacultadId(), domicilio.getProvinciaId());
        } catch (ProvinciaException e) {
            Provincia provincia = provinciaFacultadConsumer.findByUnique(
                    facultad.getApiserver(), facultad.getApiport(),
                    domicilio.getFacultadId(), domicilio.getProvinciaId());
            provincia.setUniqueId(null);
            provinciaService.add(provincia);
        }
        try {
            localidadService.findByUnique(domicilio.getFacultadId(),
                    domicilio.getProvinciaId(), domicilio.getLocalidadId());
        } catch (LocalidadException e) {
            Localidad localidad = localidadFacultadConsumer.findByUnique(
                    facultad.getApiserver(), facultad.getApiport(),
                    domicilio.getFacultadId(), domicilio.getProvinciaId(),
                    domicilio.getLocalidadId());
            localidad.setUniqueId(null);
            localidadService.add(localidad);
        }
    }
}
