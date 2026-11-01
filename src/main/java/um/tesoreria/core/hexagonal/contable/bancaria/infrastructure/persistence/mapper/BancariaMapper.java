package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.entity.BancariaEntity;

@Component
public class BancariaMapper {

    public BancariaEntity toEntity(Bancaria domain) {
        if (domain == null) return null;
        return BancariaEntity.builder()
                .bancariaId(domain.getBancariaId())
                .banco(domain.getBanco())
                .numero(domain.getNumero())
                .cbu(domain.getCbu())
                .titular(domain.getTitular())
                .cuit(domain.getCuit())
                .numeroCuenta(domain.getNumeroCuenta())
                .proveedorId(domain.getProveedorId())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .uuid(domain.getUuid())
                .build();
    }

    public Bancaria toDomain(BancariaEntity entity) {
        if (entity == null) return null;
        return Bancaria.builder()
                .bancariaId(entity.getBancariaId())
                .banco(entity.getBanco())
                .numero(entity.getNumero())
                .cbu(entity.getCbu())
                .titular(entity.getTitular())
                .cuit(entity.getCuit())
                .numeroCuenta(entity.getNumeroCuenta())
                .proveedorId(entity.getProveedorId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .uuid(entity.getUuid())
                .build();
    }
}
