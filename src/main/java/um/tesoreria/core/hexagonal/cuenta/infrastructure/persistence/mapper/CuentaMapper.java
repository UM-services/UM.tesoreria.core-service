package um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.cuenta.domain.model.CuentaSearch;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.entity.CuentaEntity;

@Component
public class CuentaMapper {

    public CuentaEntity toEntity(Cuenta domain) {
        if (domain == null) return null;
        return CuentaEntity.builder()
                .numeroCuenta(domain.getNumeroCuenta())
                .nombre(domain.getNombre())
                .integradora(domain.getIntegradora())
                .grado(domain.getGrado())
                .grado1(domain.getGrado1())
                .grado2(domain.getGrado2())
                .grado3(domain.getGrado3())
                .grado4(domain.getGrado4())
                .geograficaId(domain.getGeograficaId())
                .fechaBloqueo(domain.getFechaBloqueo())
                .visible(domain.getVisible())
                .cuentaContableId(domain.getCuentaContableId())
                .build();
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
