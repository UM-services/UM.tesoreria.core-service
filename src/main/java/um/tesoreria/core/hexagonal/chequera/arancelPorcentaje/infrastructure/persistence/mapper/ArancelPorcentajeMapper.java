package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.persistence.entity.ArancelPorcentajeEntity;

@Component
public class ArancelPorcentajeMapper {

    public ArancelPorcentaje toDomainModel(ArancelPorcentajeEntity entity) {
        if (entity == null) return null;
        return ArancelPorcentaje.builder()
                .arancelporcentajeId(entity.getArancelporcentajeId())
                .aranceltipoId(entity.getAranceltipoId())
                .productoId(entity.getProductoId())
                .porcentaje(entity.getPorcentaje())
                .build();
    }

    public ArancelPorcentajeEntity toEntity(ArancelPorcentaje domain) {
        if (domain == null) return null;
        return ArancelPorcentajeEntity.builder()
                .arancelporcentajeId(domain.getArancelporcentajeId())
                .aranceltipoId(domain.getAranceltipoId())
                .productoId(domain.getProductoId())
                .porcentaje(domain.getPorcentaje())
                .build();
    }
}
