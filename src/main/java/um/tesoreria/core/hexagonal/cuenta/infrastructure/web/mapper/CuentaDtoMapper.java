package um.tesoreria.core.hexagonal.cuenta.infrastructure.web.mapper;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.cuenta.domain.model.CuentaSearch;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.web.dto.*;
@Component
public class CuentaDtoMapper {
    public Cuenta toDomain(CuentaRequest request) {
        if (request == null) return null;
        return Cuenta.builder()
                .numeroCuenta(request.getNumeroCuenta())
                .nombre(request.getNombre())
                .integradora(request.getIntegradora())
                .grado(request.getGrado())
                .grado1(request.getGrado1())
                .grado2(request.getGrado2())
                .grado3(request.getGrado3())
                .grado4(request.getGrado4())
                .geograficaId(request.getGeograficaId())
                .fechaBloqueo(request.getFechaBloqueo())
                .visible(request.getVisible())
                .cuentaContableId(request.getCuentaContableId())
                .build();
    }
    public CuentaResponse toResponse(Cuenta domain) {
        if (domain == null) return null;
        CuentaResponse response = new CuentaResponse();
        response.setNumeroCuenta(domain.getNumeroCuenta());
        response.setNombre(domain.getNombre());
        response.setIntegradora(domain.getIntegradora());
        response.setGrado(domain.getGrado());
        response.setGrado1(domain.getGrado1());
        response.setGrado2(domain.getGrado2());
        response.setGrado3(domain.getGrado3());
        response.setGrado4(domain.getGrado4());
        response.setGeograficaId(domain.getGeograficaId());
        response.setFechaBloqueo(domain.getFechaBloqueo());
        response.setVisible(domain.getVisible());
        response.setCuentaContableId(domain.getCuentaContableId());
        return response;
    }
    public CuentaSearchResponse toSearchResponse(CuentaSearch domain) {
        if (domain == null) return null;
        CuentaSearchResponse response = new CuentaSearchResponse();
        response.setNumeroCuenta(domain.getNumeroCuenta());
        response.setNombre(domain.getNombre());
        response.setIntegradora(domain.getIntegradora());
        response.setGrado(domain.getGrado());
        response.setGrado1(domain.getGrado1());
        response.setGrado2(domain.getGrado2());
        response.setGrado3(domain.getGrado3());
        response.setGrado4(domain.getGrado4());
        response.setGeograficaId(domain.getGeograficaId());
        response.setFechaBloqueo(domain.getFechaBloqueo());
        response.setCuentaContableId(domain.getCuentaContableId());
        response.setVisible(domain.getVisible());
        response.setSearch(domain.getSearch());
        return response;
    }
}
