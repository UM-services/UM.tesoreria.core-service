package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitud;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudClaim;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioSolicitudClaimResponse;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioSolicitudResponse;

@Component
public class EnvioSolicitudDtoMapper {

    public EnvioSolicitudResponse toResponse(EnvioSolicitud solicitud) {
        return new EnvioSolicitudResponse(
                solicitud.getSolicitudId().toString(),
                solicitud.getChequeraId(),
                solicitud.getFacultadId(),
                solicitud.getTipoChequeraId(),
                solicitud.getChequeraSerieId(),
                solicitud.getLectivoId(),
                solicitud.getJobId(),
                solicitud.getEstado().name(),
                solicitud.getIntentos(),
                solicitud.getUltimoError(),
                solicitud.getEnviadoAt());
    }

    public EnvioSolicitudClaimResponse toResponse(EnvioSolicitudClaim claim) {
        return new EnvioSolicitudClaimResponse(
                claim.solicitud().getSolicitudId().toString(),
                claim.puedeEnviar(),
                claim.solicitud().getEstado().name(),
                claim.solicitud().getIntentos(),
                claim.motivo());
    }

}
