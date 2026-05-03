package um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.model.ProveedorSearch;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.entity.ProveedorEntity;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.entity.ProveedorSearchEntity;

@Component
public class ProveedorMapper {

    private final CuentaMapper cuentaMapper;

    public ProveedorMapper(CuentaMapper cuentaMapper) {
        this.cuentaMapper = cuentaMapper;
    }

    public ProveedorEntity toEntity(Proveedor domain) {
        if (domain == null) return null;
        return ProveedorEntity.builder()
                .proveedorId(domain.getProveedorId())
                .cuit(domain.getCuit())
                .nombreFantasia(domain.getNombreFantasia())
                .razonSocial(domain.getRazonSocial())
                .ordenCheque(domain.getOrdenCheque())
                .domicilio(domain.getDomicilio())
                .telefono(domain.getTelefono())
                .fax(domain.getFax())
                .celular(domain.getCelular())
                .email(domain.getEmail())
                .emailInterno(domain.getEmailInterno())
                .numeroCuenta(domain.getNumeroCuenta())
                .habilitado(domain.getHabilitado())
                .cbu(domain.getCbu())
                .build();
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
                .cbu(viewEntity.getCbu())
                .search(viewEntity.getSearch())
                .build();
    }
}
