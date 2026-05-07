package um.tesoreria.core.hexagonal.dependencia.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencia.domain.model.Dependencia;
import um.tesoreria.core.hexagonal.dependencia.infrastructure.web.dto.DependenciaRequest;
import um.tesoreria.core.hexagonal.dependencia.infrastructure.web.dto.DependenciaResponse;

@Component
public class DependenciaDtoMapper {
    public Dependencia toDomain(DependenciaRequest request) {
        if (request == null) return null;
        return Dependencia.builder()
                .dependenciaId(request.getDependenciaId())
                .nombre(request.getNombre())
                .acronimo(request.getAcronimo())
                .facultadId(request.getFacultadId())
                .geograficaId(request.getGeograficaId())
                .cuentaHonorariosPagar(request.getCuentaHonorariosPagar())
                .build();
    }

    public DependenciaResponse toResponse(Dependencia domain) {
        if (domain == null) return null;
        return DependenciaResponse.builder()
                .dependenciaId(domain.getDependenciaId())
                .nombre(domain.getNombre())
                .acronimo(domain.getAcronimo())
                .facultadId(domain.getFacultadId())
                .geograficaId(domain.getGeograficaId())
                .cuentaHonorariosPagar(domain.getCuentaHonorariosPagar())
                .fechaAuditoria(domain.getFechaAuditoria())
                .usuarioAuditoria(domain.getUsuarioAuditoria())
                .facultad(domain.getFacultad())
                .geografica(domain.getGeografica())
                .cuenta(domain.getCuenta())
                .build();
    }
}
