package um.tesoreria.core.hexagonal.ubicacion.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.ubicacion.domain.model.Ubicacion;
import um.tesoreria.core.hexagonal.ubicacion.infrastructure.persistence.entity.UbicacionEntity;

@Component
public class UbicacionMapper {
    public Ubicacion toDomainModel(UbicacionEntity entity) {
        if (entity == null) return null;
        return Ubicacion.builder()
                .ubicacionId(entity.getUbicacionId())
                .nombre(entity.getNombre())
                .dependenciaId(entity.getDependenciaId())
                .geograficaId(entity.getDependencia() != null ? entity.getDependencia().getGeograficaId() : null)
                .build();
    }

    public UbicacionEntity toEntity(Ubicacion domain) {
        if (domain == null) return null;
        UbicacionEntity entity = new UbicacionEntity();
        entity.setUbicacionId(domain.getUbicacionId());
        entity.setNombre(domain.getNombre());
        entity.setDependenciaId(domain.getDependenciaId());
        return entity;
    }
}
