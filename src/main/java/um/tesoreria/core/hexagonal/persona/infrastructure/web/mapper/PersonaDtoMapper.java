package um.tesoreria.core.hexagonal.persona.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.persona.domain.model.DeudaExamen;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.persona.infrastructure.web.dto.DeudaExamenResponse;
import um.tesoreria.core.hexagonal.persona.infrastructure.web.dto.PersonaRequest;
import um.tesoreria.core.hexagonal.persona.infrastructure.web.dto.PersonaResponse;

@Component
public class PersonaDtoMapper {

    public Persona toDomain(PersonaRequest request) {
        if (request == null) return null;
        return Persona.builder()
                .uniqueId(request.getUniqueId())
                .personaId(request.getPersonaId())
                .documentoId(request.getDocumentoId())
                .apellido(request.getApellido())
                .nombre(request.getNombre())
                .sexo(request.getSexo())
                .primero(request.getPrimero())
                .cuit(request.getCuit())
                .cbu(request.getCbu())
                .password(request.getPassword())
                .hpum(request.getHpum())
                .build();
    }

    public PersonaResponse toResponse(Persona domain) {
        if (domain == null) return null;
        return PersonaResponse.builder()
                .uniqueId(domain.getUniqueId())
                .personaId(domain.getPersonaId())
                .documentoId(domain.getDocumentoId())
                .apellido(domain.getApellido())
                .nombre(domain.getNombre())
                .sexo(domain.getSexo())
                .primero(domain.getPrimero())
                .cuit(domain.getCuit())
                .cbu(domain.getCbu())
                .password(domain.getPassword())
                .hpum(domain.getHpum())
                .build();
    }

    public DeudaExamenResponse toDeudaExamenResponse(DeudaExamen domain) {
        if (domain == null) return null;
        return DeudaExamenResponse.builder()
                .autorizadoRendir(domain.getAutorizadoRendir())
                .matriculaPagada(domain.getMatriculaPagada())
                .cuotasAdeudadas(domain.getCuotasAdeudadas())
                .importeAdeudado(domain.getImporteAdeudado())
                .habilitadoTesoreria(domain.getHabilitadoTesoreria())
                .build();
    }
}
