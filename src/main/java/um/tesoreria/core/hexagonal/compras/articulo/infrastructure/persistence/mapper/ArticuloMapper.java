package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ArticuloSearch;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity.ArticuloEntity;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity.ArticuloKey;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;

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
        ArticuloEntity.ArticuloEntityBuilder builder = ArticuloEntity.builder()
                .articuloId(domain.getArticuloId())
                .numeroCuenta(domain.getNumeroCuenta());
        
        if (domain.getNombre() != null) builder.nombre(domain.getNombre());
        if (domain.getDescripcion() != null) builder.descripcion(domain.getDescripcion());
        if (domain.getUnidad() != null) builder.unidad(domain.getUnidad());
        if (domain.getPrecio() != null) builder.precio(domain.getPrecio());
        if (domain.getInventariable() != null) builder.inventariable(domain.getInventariable());
        if (domain.getStockMinimo() != null) builder.stockMinimo(domain.getStockMinimo());
        if (domain.getTipo() != null) builder.tipo(domain.getTipo());
        if (domain.getDirecto() != null) builder.directo(domain.getDirecto());
        if (domain.getHabilitado() != null) builder.habilitado(domain.getHabilitado());
        
        return builder.build();
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
                .numeroCuenta(entity.getNumeroCuenta())
                .tipo(entity.getTipo())
                .directo(entity.getDirecto())
                .habilitado(entity.getHabilitado())
                .search(entity.getSearch())
                .build();
    }
}
