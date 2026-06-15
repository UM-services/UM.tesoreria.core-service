package um.tesoreria.core.hexagonal.domicilio.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.web.dto.DomicilioRequest;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.web.dto.DomicilioResponse;

@Component
public class DomicilioDtoMapper {

    public Domicilio toDomain(DomicilioRequest request) {
        if (request == null) return null;
        return Domicilio.builder()
                .domicilioId(request.getDomicilioId())
                .personaId(request.getPersonaId())
                .documentoId(request.getDocumentoId())
                .fecha(request.getFecha())
                .calle(request.getCalle())
                .puerta(request.getPuerta())
                .piso(request.getPiso())
                .dpto(request.getDpto())
                .telefono(request.getTelefono())
                .movil(request.getMovil())
                .observaciones(request.getObservaciones())
                .codigoPostal(request.getCodigoPostal())
                .facultadId(request.getFacultadId())
                .provinciaId(request.getProvinciaId())
                .localidadId(request.getLocalidadId())
                .emailPersonal(request.getEmailPersonal())
                .emailInstitucional(request.getEmailInstitucional())
                .laboral(request.getLaboral())
                .build();
    }

    public DomicilioResponse toResponse(Domicilio domain) {
        if (domain == null) return null;
        return DomicilioResponse.builder()
                .domicilioId(domain.getDomicilioId())
                .personaId(domain.getPersonaId())
                .documentoId(domain.getDocumentoId())
                .fecha(domain.getFecha())
                .calle(domain.getCalle())
                .puerta(domain.getPuerta())
                .piso(domain.getPiso())
                .dpto(domain.getDpto())
                .telefono(domain.getTelefono())
                .movil(domain.getMovil())
                .observaciones(domain.getObservaciones())
                .codigoPostal(domain.getCodigoPostal())
                .facultadId(domain.getFacultadId())
                .provinciaId(domain.getProvinciaId())
                .localidadId(domain.getLocalidadId())
                .emailPersonal(domain.getEmailPersonal())
                .emailInstitucional(domain.getEmailInstitucional())
                .laboral(domain.getLaboral())
                .emailPagador(domain.getEmailPagador())
                .build();
    }
}
