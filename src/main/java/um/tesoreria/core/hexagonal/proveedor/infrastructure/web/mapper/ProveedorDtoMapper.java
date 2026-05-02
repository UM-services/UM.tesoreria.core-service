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
        ProveedorResponse response = new ProveedorResponse();
        response.setProveedorId(domain.getProveedorId());
        response.setCuit(domain.getCuit());
        response.setNombreFantasia(domain.getNombreFantasia());
        response.setRazonSocial(domain.getRazonSocial());
        response.setOrdenCheque(domain.getOrdenCheque());
        response.setDomicilio(domain.getDomicilio());
        response.setTelefono(domain.getTelefono());
        response.setFax(domain.getFax());
        response.setCelular(domain.getCelular());
        response.setEmail(domain.getEmail());
        response.setEmailInterno(domain.getEmailInterno());
        response.setNumeroCuenta(domain.getNumeroCuenta());
        response.setHabilitado(domain.getHabilitado());
        response.setCbu(domain.getCbu());
        return response;
    }

    public ProveedorSearchResponse toSearchResponse(ProveedorSearch domain) {
        if (domain == null) return null;
        ProveedorSearchResponse response = new ProveedorSearchResponse();
        response.setProveedorId(domain.getProveedorId());
        response.setCuit(domain.getCuit());
        response.setNombreFantasia(domain.getNombreFantasia());
        response.setRazonSocial(domain.getRazonSocial());
        response.setOrdenCheque(domain.getOrdenCheque());
        response.setDomicilio(domain.getDomicilio());
        response.setTelefono(domain.getTelefono());
        response.setFax(domain.getFax());
        response.setCelular(domain.getCelular());
        response.setEmail(domain.getEmail());
        response.setCuenta(domain.getCuenta());
        response.setHabilitado(domain.getHabilitado());
        response.setSearch(domain.getSearch());
        return response;
    }
}
