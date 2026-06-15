package um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.web.dto.MercadoPagoContextRequest;
import um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.web.dto.MercadoPagoContextResponse;

@Component
public class MercadoPagoContextDtoMapper {

    public MercadoPagoContext toDomain(MercadoPagoContextRequest request) {
        if (request == null) return null;
        return MercadoPagoContext.builder()
                .chequeraCuotaId(request.getChequeraCuotaId())
                .initPoint(request.getInitPoint())
                .fechaVencimiento(request.getFechaVencimiento())
                .importe(request.getImporte())
                .changed(request.getChanged())
                .lastVencimientoUpdated(request.getLastVencimientoUpdated())
                .preferenceId(request.getPreferenceId())
                .preference(request.getPreference())
                .activo(request.getActivo())
                .chequeraPagoId(request.getChequeraPagoId())
                .idMercadoPago(request.getIdMercadoPago())
                .status(request.getStatus())
                .fechaPago(request.getFechaPago())
                .fechaAcreditacion(request.getFechaAcreditacion())
                .importePagado(request.getImportePagado())
                .payment(request.getPayment())
                .build();
    }

    public MercadoPagoContextResponse toResponse(MercadoPagoContext domain) {
        if (domain == null) return null;
        return MercadoPagoContextResponse.builder()
                .mercadoPagoContextId(domain.getMercadoPagoContextId())
                .chequeraCuotaId(domain.getChequeraCuotaId())
                .initPoint(domain.getInitPoint())
                .fechaVencimiento(domain.getFechaVencimiento())
                .importe(domain.getImporte())
                .changed(domain.getChanged())
                .lastVencimientoUpdated(domain.getLastVencimientoUpdated())
                .preferenceId(domain.getPreferenceId())
                .preference(domain.getPreference())
                .activo(domain.getActivo())
                .chequeraPagoId(domain.getChequeraPagoId())
                .idMercadoPago(domain.getIdMercadoPago())
                .status(domain.getStatus())
                .fechaPago(domain.getFechaPago())
                .fechaAcreditacion(domain.getFechaAcreditacion())
                .importePagado(domain.getImportePagado())
                .payment(domain.getPayment())
                .build();
    }

}
