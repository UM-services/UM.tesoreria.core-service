package um.tesoreria.core.hexagonal.proveedor.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.model.ProveedorSearch;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.web.dto.ProveedorRequest;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.web.dto.ProveedorResponse;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.web.dto.ProveedorSearchResponse;

@Component
public class ProveedorDtoMapper {

    public Proveedor toDomain(ProveedorRequest request) {
        if (request == null) return null;
        return Proveedor.builder()
                .cuit(request.getCuit())
                .nombreFantasia(request.getNombreFantasia())
                .razonSocial(request.getRazonSocial())
                .ordenCheque(request.getOrdenCheque())
                .domicilio(request.getDomicilio())
                .telefono(request.getTelefono())
                .fax(request.getFax())
                .celular(request.getCelular())
                .email(request.getEmail())
                .emailInterno(request.getEmailInterno())
                .numeroCuenta(request.getNumeroCuenta())
                .habilitado(request.getHabilitado())
                .cbu(request.getCbu())
                .build();
    }

    public ProveedorResponse toResponse(Proveedor domain) {
        if (domain == null) return null;
        return ProveedorResponse.builder()
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
                .cuenta(domain.getCuenta())
                .build();
    }

    public ProveedorSearchResponse toSearchResponse(ProveedorSearch domain) {
        if (domain == null) return null;
        return ProveedorSearchResponse.builder()
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
                .numeroCuenta(domain.getNumeroCuenta())
                .habilitado(domain.getHabilitado())
                .cbu(domain.getCbu())
                .search(domain.getSearch())
                .build();
    }
}
