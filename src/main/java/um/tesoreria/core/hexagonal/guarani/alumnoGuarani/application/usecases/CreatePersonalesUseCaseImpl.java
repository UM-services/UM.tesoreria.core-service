package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.hexagonal.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.ports.in.CreatePersonalesUseCase;
import um.tesoreria.core.hexagonal.personas.documento.application.exception.DocumentoException;
import um.tesoreria.core.hexagonal.personas.documento.application.service.DocumentoService;
import um.tesoreria.core.hexagonal.personas.persona.application.exception.PersonaException;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatePersonalesUseCaseImpl implements CreatePersonalesUseCase {

    private final DocumentoService documentoService;
    private final PersonaService personaService;
    private final DomicilioService domicilioService;

    @Override
    @Transactional
    public Boolean createPersonales(AlumnoGuarani alumnoGuarani) {
        try {
            BigDecimal personaId = new BigDecimal(alumnoGuarani.getPersonaRel().getDocumentoPrincipalRel().getNroDocumento());
            Integer documentoId = documentoService.findFirstByGuaraniTipoDocumento(
                    alumnoGuarani.getPersonaRel().getDocumentoPrincipalRel().getTipoDocumentoRel().getTipoDocumento())
                    .getDocumentoId();

            Persona persona;
            try {
                persona = personaService.findByUnique(personaId, documentoId);
            } catch (PersonaException e) {
                log.debug("Persona no encontrada, se intentará crear: {}", e.getMessage());
                persona = Persona.builder()
                        .personaId(personaId)
                        .documentoId(documentoId)
                        .apellido(alumnoGuarani.getPersonaRel().getApellido())
                        .nombre(alumnoGuarani.getPersonaRel().getNombres())
                        .cbu("")
                        .cuit("")
                        .hpum((byte) 0)
                        .primero((byte) 0)
                        .sexo(alumnoGuarani.getPersonaRel().getSexo())
                        .build();
                persona = personaService.create(persona);
                if (persona == null) {
                    log.error("La creación de la persona devolvió null");
                    return false;
                }
            }
            log.debug("Persona -> {}", persona.jsonify());

            Domicilio domicilio;
            try {
                domicilio = domicilioService.findByUnique(persona.getPersonaId(), persona.getDocumentoId());
            } catch (DomicilioException e) {
                log.debug("Domicilio no encontrado, se intentará crear: {}", e.getMessage());
                domicilio = Domicilio.builder()
                        .personaId(persona.getPersonaId())
                        .documentoId(persona.getDocumentoId())
                        .fecha(OffsetDateTime.now())
                        .emailPersonal(alumnoGuarani.getPersonaRel().getContactos().getFirst().getEmail())
                        .emailInstitucional("")
                        .calle("")
                        .puerta("")
                        .codigoPostal("")
                        .piso("")
                        .dpto("")
                        .laboral("")
                        .telefono("")
                        .movil("")
                        .observaciones("")
                        .build();
                domicilio = domicilioService.create(domicilio);
                if (domicilio == null) {
                    log.error("La creación del domicilio devolvió null");
                    return false;
                }
            }
            log.debug("Domicilio -> {}", domicilio.jsonify());
            return true;
        } catch (DocumentoException e) {
            log.error("No se pudo obtener el tipo de documento", e);
            return false;
        } catch (RuntimeException e) {
            log.error("No se pudo completar la creación de personales", e);
            return false;
        }
    }

}
