package um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.model.ProveedorSearch;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.persistence.entity.ProveedorEntity;

@Component
public class ProveedorMapper {

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
                .build();
    }

    public ProveedorSearch toSearchDomain(um.tesoreria.core.model.view.ProveedorSearch viewEntity) {
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
                .cuenta(viewEntity.getCuenta())
                .habilitado(viewEntity.getHabilitado())
                .search(viewEntity.getSearch())
                .build();
    }
}
