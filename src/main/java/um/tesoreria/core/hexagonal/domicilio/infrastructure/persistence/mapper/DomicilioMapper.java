package um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.persistence.entity.DomicilioEntity;

@Component
public class DomicilioMapper {

    public Domicilio toDomainModel(DomicilioEntity entity) {
        if (entity == null) return null;
        return Domicilio.builder()
                .domicilioId(entity.getDomicilioId())
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
                .emailPagador(entity.getEmailPagador())
                .build();
    }

    public DomicilioEntity toEntity(Domicilio domain) {
        if (domain == null) return null;
        return DomicilioEntity.builder()
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
                .build();
    }
}
