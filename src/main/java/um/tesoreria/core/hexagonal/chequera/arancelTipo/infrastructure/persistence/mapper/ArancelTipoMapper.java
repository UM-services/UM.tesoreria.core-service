package um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;

@Component
public class ArancelTipoMapper {

    public ArancelTipo toDomainModel(ArancelTipoEntity entity) {
        if (entity == null) return null;
        return ArancelTipo.builder()
                .arancelTipoId(entity.getArancelTipoId())
                .descripcion(entity.getDescripcion())
                .medioArancel(entity.getMedioArancel())
                .arancelTipoIdCompleto(entity.getArancelTipoIdCompleto())
                .habilitado(entity.getHabilitado())
                .build();
    }

    public ArancelTipoEntity toEntity(ArancelTipo domain) {
        if (domain == null) return null;
        ArancelTipoEntity.ArancelTipoEntityBuilder builder = ArancelTipoEntity.builder()
                .arancelTipoId(domain.getArancelTipoId())
                .arancelTipoIdCompleto(domain.getArancelTipoIdCompleto());
        
        if (domain.getDescripcion() != null) builder.descripcion(domain.getDescripcion());
        if (domain.getMedioArancel() != null) builder.medioArancel(domain.getMedioArancel());
        if (domain.getHabilitado() != null) builder.habilitado(domain.getHabilitado());
        
        return builder.build();
    }
}
