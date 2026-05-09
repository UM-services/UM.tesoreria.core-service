package um.tesoreria.core.hexagonal.facultad.infrastructure.web.mapper;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.facultad.infrastructure.web.dto.FacultadResponse;

@Component
public class FacultadDtoMapper {
    public FacultadResponse toResponse(Facultad domain) {
        if (domain == null) return null;
        return FacultadResponse.builder()
                .facultadId(domain.getFacultadId())
                .nombre(domain.getNombre())
                .codigoempresa(domain.getCodigoempresa())
                .server(domain.getServer())
                .dbadm(domain.getDbadm())
                .dsn(domain.getDsn())
                .cuentacontable(domain.getCuentacontable())
                .apiserver(domain.getApiserver())
                .apiport(domain.getApiport())
                .build();
    }
}
