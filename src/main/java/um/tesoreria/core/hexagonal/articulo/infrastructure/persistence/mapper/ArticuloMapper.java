package um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.articulo.domain.model.ArticuloSearch;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.entity.ArticuloEntity;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.entity.ArticuloKey;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.mapper.CuentaMapper;

@Component
public class ArticuloMapper {

    private final CuentaMapper cuentaMapper;

    public ArticuloMapper(CuentaMapper cuentaMapper) {
        this.cuentaMapper = cuentaMapper;
    }

    public Articulo toDomainModel(ArticuloEntity entity) {
        if (entity == null) return null;
        return Articulo.builder()
                .articuloId(entity.getArticuloId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .unidad(entity.getUnidad())
                .precio(entity.getPrecio())
                .inventariable(entity.getInventariable())
                .stockMinimo(entity.getStockMinimo())
                .numeroCuenta(entity.getNumeroCuenta())
                .tipo(entity.getTipo())
                .directo(entity.getDirecto())
                .habilitado(entity.getHabilitado())
                .cuenta(entity.getCuenta() != null ? cuentaMapper.toDomain(entity.getCuenta()) : null)
                .build();
    }

    public ArticuloEntity toEntity(Articulo domain) {
        if (domain == null) return null;
        return ArticuloEntity.builder()
                .articuloId(domain.getArticuloId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .unidad(domain.getUnidad())
                .precio(domain.getPrecio())
                .inventariable(domain.getInventariable())
                .stockMinimo(domain.getStockMinimo())
                .numeroCuenta(domain.getNumeroCuenta())
                .tipo(domain.getTipo())
                .directo(domain.getDirecto())
                .habilitado(domain.getHabilitado())
                .build();
    }

    public ArticuloSearch toSearchDomain(ArticuloKey entity) {
        if (entity == null) return null;
        return ArticuloSearch.builder()
                .articuloId(entity.getArticuloId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .unidad(entity.getUnidad())
                .precio(entity.getPrecio())
                .inventariable(entity.getInventariable())
                .stockMinimo(entity.getStockMinimo())
                .cuenta(entity.getCuenta())
                .tipo(entity.getTipo())
                .directo(entity.getDirecto())
                .habilitado(entity.getHabilitado())
                .search(entity.getSearch())
                .build();
    }
}
