package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto.ReservaVacanteRequest;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto.ReservaVacanteResponse;

@Component
public class ReservaVacanteDtoMapper {

    public ReservaVacante toDomain(ReservaVacanteRequest request) {
        if (request == null) return null;
        return ReservaVacante.builder()
                .campanhaId(request.getCampanhaId())
                .build();
    }

    public ReservaVacanteResponse toResponse(ReservaVacante domain) {
        if (domain == null) return null;
        return ReservaVacanteResponse.builder()
                .reservaVacanteId(domain.getReservaVacanteId())
                .tipoDocumento(domain.getPersona().getDocumentoId())
                .numeroDocumento(domain.getPersona().getPersonaId().toPlainString())
                .nombre(domain.getPersona().getNombre())
                .apellido(domain.getPersona().getApellido())
                .email(domain.getDomicilio().getEmailPersonal())
                .campanhaId(domain.getCampanhaId())
                .created(domain.getCreated())
                .estado(domain.getEstado())
                .updated(domain.getUpdated())
                .build();
    }
}
