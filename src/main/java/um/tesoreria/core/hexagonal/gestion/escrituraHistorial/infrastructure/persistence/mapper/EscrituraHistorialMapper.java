package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraHistorial;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.entity.EscrituraHistorialEntity;

@Component
public class EscrituraHistorialMapper {

    public EscrituraHistorial toDomain(EscrituraHistorialEntity entity) {
        if (entity == null) {
            return null;
        }
        return EscrituraHistorial.builder()
                .escrituraHistorialId(entity.getEscrituraHistorialId())
                .fecha(entity.getFecha())
                .operacion(entity.getOperacion())
                .entidad(entity.getEntidad())
                .entidadClave(entity.getEntidadClave())
                .valorAnterior(entity.getValorAnterior())
                .valorNuevo(entity.getValorNuevo())
                .build();
    }

    public EscrituraHistorialEntity toEntity(EscrituraHistorial domain) {
        if (domain == null) {
            return null;
        }
        return EscrituraHistorialEntity.builder()
                .escrituraHistorialId(domain.getEscrituraHistorialId())
                .operacion(domain.getOperacion())
                .entidad(domain.getEntidad())
                .entidadClave(domain.getEntidadClave())
                .valorAnterior(domain.getValorAnterior())
                .valorNuevo(domain.getValorNuevo())
                .build();
    }
}
