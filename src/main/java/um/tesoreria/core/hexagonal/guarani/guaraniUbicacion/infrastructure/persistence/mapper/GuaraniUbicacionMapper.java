package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.persistence.entity.GuaraniUbicacionEntity;

@Component
public class GuaraniUbicacionMapper {
    public GuaraniUbicacion toDomain(GuaraniUbicacionEntity entity) {
        if (entity == null) {
            return null;
        }
        return GuaraniUbicacion.builder()
                .guaraniUbicacionId(entity.getGuaraniUbicacionId())
                .ubicacion(entity.getUbicacion())
                .geograficaId(entity.getGeograficaId())
                .build();
    }

    public GuaraniUbicacionEntity toEntity(GuaraniUbicacion domain) {
        if (domain == null) {
            return null;
        }
        return GuaraniUbicacionEntity.builder()
                .guaraniUbicacionId(domain.getGuaraniUbicacionId())
                .ubicacion(domain.getUbicacion())
                .geograficaId(domain.getGeograficaId())
                .build();
    }
}
