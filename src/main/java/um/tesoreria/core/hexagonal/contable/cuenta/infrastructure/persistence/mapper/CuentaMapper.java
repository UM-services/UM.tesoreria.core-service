package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaSearch;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;

@Component
public class CuentaMapper {

    public CuentaEntity toEntity(Cuenta domain) {
        if (domain == null) return null;
        CuentaEntity.CuentaEntityBuilder builder = CuentaEntity.builder()
                .numeroCuenta(domain.getNumeroCuenta())
                .grado1(domain.getGrado1())
                .grado2(domain.getGrado2())
                .grado3(domain.getGrado3())
                .grado4(domain.getGrado4())
                .geograficaId(domain.getGeograficaId())
                .fechaBloqueo(domain.getFechaBloqueo())
                .cuentaContableId(domain.getCuentaContableId());
        
        if (domain.getNombre() != null) builder.nombre(domain.getNombre());
        if (domain.getIntegradora() != null) builder.integradora(domain.getIntegradora());
        if (domain.getGrado() != null) builder.grado(domain.getGrado());
        if (domain.getVisible() != null) builder.visible(domain.getVisible());
        
        return builder.build();
    }

    public Cuenta toDomain(CuentaEntity entity) {
        if (entity == null) return null;
        return Cuenta.builder()
                .numeroCuenta(entity.getNumeroCuenta())
                .nombre(entity.getNombre())
                .integradora(entity.getIntegradora())
                .grado(entity.getGrado())
                .grado1(entity.getGrado1())
                .grado2(entity.getGrado2())
                .grado3(entity.getGrado3())
                .grado4(entity.getGrado4())
                .geograficaId(entity.getGeograficaId())
                .fechaBloqueo(entity.getFechaBloqueo())
                .visible(entity.getVisible())
                .cuentaContableId(entity.getCuentaContableId())
                .build();
    }

    public CuentaSearch toSearchDomain(um.tesoreria.core.model.view.CuentaSearch entity) {
        if (entity == null) return null;
        return CuentaSearch.builder()
                .numeroCuenta(entity.getNumeroCuenta())
                .nombre(entity.getNombre())
                .integradora(entity.getIntegradora())
                .grado(entity.getGrado())
                .grado1(entity.getGrado1())
                .grado2(entity.getGrado2())
                .grado3(entity.getGrado3())
                .grado4(entity.getGrado4())
                .geograficaId(entity.getGeograficaId())
                .fechaBloqueo(entity.getFechaBloqueo())
                .cuentaContableId(entity.getCuentaContableId())
                .visible(entity.getVisible())
                .search(entity.getSearch())
                .build();
    }
}
