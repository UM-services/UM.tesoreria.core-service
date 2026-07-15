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
        DomicilioEntity.DomicilioEntityBuilder builder = DomicilioEntity.builder()
                .domicilioId(domain.getDomicilioId())
                .personaId(domain.getPersonaId())
                .documentoId(domain.getDocumentoId())
                .facultadId(domain.getFacultadId())
                .provinciaId(domain.getProvinciaId())
                .localidadId(domain.getLocalidadId());
        
        if (domain.getFecha() != null) builder.fecha(domain.getFecha());
        if (domain.getCalle() != null) builder.calle(domain.getCalle());
        if (domain.getPuerta() != null) builder.puerta(domain.getPuerta());
        if (domain.getPiso() != null) builder.piso(domain.getPiso());
        if (domain.getDpto() != null) builder.dpto(domain.getDpto());
        if (domain.getTelefono() != null) builder.telefono(domain.getTelefono());
        if (domain.getMovil() != null) builder.movil(domain.getMovil());
        if (domain.getObservaciones() != null) builder.observaciones(domain.getObservaciones());
        if (domain.getCodigoPostal() != null) builder.codigoPostal(domain.getCodigoPostal());
        if (domain.getEmailPersonal() != null) builder.emailPersonal(domain.getEmailPersonal());
        if (domain.getEmailInstitucional() != null) builder.emailInstitucional(domain.getEmailInstitucional());
        if (domain.getLaboral() != null) builder.laboral(domain.getLaboral());
        
        return builder.build();
    }
}
