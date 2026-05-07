package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.mapper.ArticuloMapper;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.ubicacion.infrastructure.persistence.mapper.UbicacionMapper;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;

@Component
@RequiredArgsConstructor
public class UbicacionArticuloMapper {
    private final UbicacionMapper ubicacionMapper;
    private final ArticuloMapper articuloMapper;
    private final CuentaMapper cuentaMapper;

    public UbicacionArticulo toDomainModel(UbicacionArticuloEntity entity) {
        if (entity == null) return null;
        return UbicacionArticulo.builder()
                .ubicacionArticuloId(entity.getUbicacionArticuloId())
                .ubicacionId(entity.getUbicacionId())
                .articuloId(entity.getArticuloId())
                .numeroCuenta(entity.getNumeroCuenta())
                .ubicacion(ubicacionMapper.toDomainModel(entity.getUbicacion()))
                .articulo(articuloMapper.toDomainModel(entity.getArticulo()))
                .cuenta(cuentaMapper.toDomain(entity.getCuenta()))
                .build();
    }

    public UbicacionArticuloEntity toEntity(UbicacionArticulo domain) {
        if (domain == null) return null;
        UbicacionArticuloEntity entity = new UbicacionArticuloEntity();
        entity.setUbicacionArticuloId(domain.getUbicacionArticuloId());
        entity.setUbicacionId(domain.getUbicacionId());
        entity.setArticuloId(domain.getArticuloId());
        entity.setNumeroCuenta(domain.getNumeroCuenta());
        return entity;
    }
}
