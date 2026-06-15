package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.MercadoPagoContextService;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto.ReservaVacanteRequest;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto.ReservaVacanteResponse;

@Component
@Slf4j
public class ReservaVacanteDtoMapper {

    private final MercadoPagoContextService mercadoPagoContextService;

    public ReservaVacanteDtoMapper(MercadoPagoContextService mercadoPagoContextService) {
        this.mercadoPagoContextService = mercadoPagoContextService;
    }

    public ReservaVacante toDomain(ReservaVacanteRequest request) {
        if (request == null) return null;
        return ReservaVacante.builder()
                .campanhaId(request.getCampanhaId())
                .build();
    }

    public ReservaVacanteResponse toResponse(ReservaVacante domain) {
        log.debug("\n\nProcessing ReservaVacanteDtoMapper\n\n");
        if (domain == null) return null;
        var response = ReservaVacanteResponse.builder()
                .reservaVacanteId(domain.getReservaVacanteId())
                .tipoDocumento(domain.getPersona().getDocumentoId())
                .numeroDocumento(domain.getPersona().getPersonaId().toPlainString())
                .nombre(domain.getPersona().getNombre())
                .apellido(domain.getPersona().getApellido())
                .email(domain.getDomicilio().getEmailPersonal())
                .campanhaId(domain.getCampanhaId())
                .estado(domain.getEstado())
                .importe(domain.getImporte())
                .vencimiento(domain.getVencimiento())
                .initPoint(mercadoPagoContextService.findActiveByReservaVacanteId(domain.getReservaVacanteId()).getInitPoint())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .build();
        log.debug("response -> {}", response.jsonify());
        return response;
    }
}
