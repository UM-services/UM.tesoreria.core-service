package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorSearch;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.entity.ProveedorEntity;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.entity.ProveedorSearchEntity;

@Component
public class ProveedorMapper {

    private final CuentaMapper cuentaMapper;

    public ProveedorMapper(CuentaMapper cuentaMapper) {
        this.cuentaMapper = cuentaMapper;
    }

    public ProveedorEntity toEntity(Proveedor domain) {
        if (domain == null) return null;
        ProveedorEntity.ProveedorEntityBuilder builder = ProveedorEntity.builder()
                .proveedorId(domain.getProveedorId())
                .numeroCuenta(domain.getNumeroCuenta());
        
        if (domain.getCuit() != null) builder.cuit(domain.getCuit());
        if (domain.getNombreFantasia() != null) builder.nombreFantasia(domain.getNombreFantasia());
        if (domain.getRazonSocial() != null) builder.razonSocial(domain.getRazonSocial());
        if (domain.getOrdenCheque() != null) builder.ordenCheque(domain.getOrdenCheque());
        if (domain.getDomicilio() != null) builder.domicilio(domain.getDomicilio());
        if (domain.getTelefono() != null) builder.telefono(domain.getTelefono());
        if (domain.getFax() != null) builder.fax(domain.getFax());
        if (domain.getCelular() != null) builder.celular(domain.getCelular());
        if (domain.getEmail() != null) builder.email(domain.getEmail());
        if (domain.getEmailInterno() != null) builder.emailInterno(domain.getEmailInterno());
        if (domain.getHabilitado() != null) builder.habilitado(domain.getHabilitado());
        if (domain.getCbu() != null) builder.cbu(domain.getCbu());
        
        return builder.build();
    }

    public Proveedor toDomainModel(ProveedorEntity entity) {
        if (entity == null) return null;
        return Proveedor.builder()
                .proveedorId(entity.getProveedorId())
                .cuit(entity.getCuit())
                .nombreFantasia(entity.getNombreFantasia())
                .razonSocial(entity.getRazonSocial())
                .ordenCheque(entity.getOrdenCheque())
                .domicilio(entity.getDomicilio())
                .telefono(entity.getTelefono())
                .fax(entity.getFax())
                .celular(entity.getCelular())
                .email(entity.getEmail())
                .emailInterno(entity.getEmailInterno())
                .numeroCuenta(entity.getNumeroCuenta())
                .habilitado(entity.getHabilitado())
                .cbu(entity.getCbu())
                .cuenta(cuentaMapper.toDomain(entity.getCuenta()))
                .build();
    }

    public ProveedorSearch toSearchDomain(ProveedorSearchEntity viewEntity) {
        if (viewEntity == null) return null;
        return ProveedorSearch.builder()
                .proveedorId(viewEntity.getProveedorId())
                .cuit(viewEntity.getCuit())
                .nombreFantasia(viewEntity.getNombreFantasia())
                .razonSocial(viewEntity.getRazonSocial())
                .ordenCheque(viewEntity.getOrdenCheque())
                .domicilio(viewEntity.getDomicilio())
                .telefono(viewEntity.getTelefono())
                .fax(viewEntity.getFax())
                .celular(viewEntity.getCelular())
                .email(viewEntity.getEmail())
                .numeroCuenta(viewEntity.getNumeroCuenta())
                .habilitado(viewEntity.getHabilitado())
                .search(viewEntity.getSearch())
                .build();
    }
}
