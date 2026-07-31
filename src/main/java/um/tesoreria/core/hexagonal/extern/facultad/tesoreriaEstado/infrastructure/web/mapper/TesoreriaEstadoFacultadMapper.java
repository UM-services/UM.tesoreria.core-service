package um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.model.TesoreriaEstadoFacultad;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.infrastructure.web.dto.TesoreriaEstadoFacultadResponse;

@Component
public class TesoreriaEstadoFacultadMapper {

    public TesoreriaEstadoFacultad toDomain(TesoreriaEstadoFacultadResponse response) {
        if (response == null) return null;
        TesoreriaEstadoFacultad.TesoreriaEstadoFacultadBuilder builder = TesoreriaEstadoFacultad.builder()
                .tesoreriaEstadoId(response.getTesoreriaEstadoId())
                .facultadId(response.getFacultadId())
                .personaId(response.getPersonaId())
                .documentoId(response.getDocumentoId())
                .deuda(response.getDeuda())
                .fechaTope(response.getFechaTope());

        if (response.getManual() != null) {
            builder.manual(response.getManual());
        }
        if (response.getImportado() != null) {
            builder.importado(response.getImportado());
        }
        if (response.getObservaciones() != null) {
            builder.observaciones(response.getObservaciones());
        }
        if (response.getUuid() != null) {
            builder.uuid(response.getUuid());
        }

        return builder.build();
    }
}
