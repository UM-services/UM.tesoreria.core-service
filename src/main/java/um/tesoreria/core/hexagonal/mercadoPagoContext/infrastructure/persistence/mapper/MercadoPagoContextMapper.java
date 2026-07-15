package um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.mapper.ChequeraCuotaMapper;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.entity.MercadoPagoContextEntity;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.mapper.ReservaVacanteMapper;

@Component
@RequiredArgsConstructor
public class MercadoPagoContextMapper {

    private final ChequeraCuotaMapper chequeraCuotaMapper;
    private final ReservaVacanteMapper reservaVacanteMapper;

    public MercadoPagoContextEntity toEntity(MercadoPagoContext domain) {
        if (domain == null) return null;
        MercadoPagoContextEntity.MercadoPagoContextEntityBuilder builder = MercadoPagoContextEntity.builder()
                .mercadoPagoContextId(domain.getMercadoPagoContextId())
                .chequeraCuotaId(domain.getChequeraCuotaId())
                .reservaVacanteId(domain.getReservaVacanteId())
                .initPoint(domain.getInitPoint())
                .fechaVencimiento(domain.getFechaVencimiento())
                .lastVencimientoUpdated(domain.getLastVencimientoUpdated())
                .preferenceId(domain.getPreferenceId())
                .preference(domain.getPreference())
                .chequeraPagoId(domain.getChequeraPagoId())
                .idMercadoPago(domain.getIdMercadoPago())
                .status(domain.getStatus())
                .fechaPago(domain.getFechaPago())
                .fechaAcreditacion(domain.getFechaAcreditacion())
                .payment(domain.getPayment());

        if (domain.getImporte() != null) builder.importe(domain.getImporte());
        if (domain.getChanged() != null) builder.changed(domain.getChanged());
        if (domain.getActivo() != null) builder.activo(domain.getActivo());
        if (domain.getImportePagado() != null) builder.importePagado(domain.getImportePagado());

        return builder.build();
    }

    public MercadoPagoContext toDomainModel(MercadoPagoContextEntity entity) {
        if (entity == null) return null;
        return MercadoPagoContext.builder()
                .mercadoPagoContextId(entity.getMercadoPagoContextId())
                .chequeraCuotaId(entity.getChequeraCuotaId())
                .reservaVacanteId(entity.getReservaVacanteId())
                .initPoint(entity.getInitPoint())
                .fechaVencimiento(entity.getFechaVencimiento())
                .importe(entity.getImporte())
                .changed(entity.getChanged())
                .lastVencimientoUpdated(entity.getLastVencimientoUpdated())
                .preferenceId(entity.getPreferenceId())
                .preference(entity.getPreference())
                .activo(entity.getActivo())
                .chequeraPagoId(entity.getChequeraPagoId())
                .idMercadoPago(entity.getIdMercadoPago())
                .status(entity.getStatus())
                .fechaPago(entity.getFechaPago())
                .fechaAcreditacion(entity.getFechaAcreditacion())
                .importePagado(entity.getImportePagado())
                .payment(entity.getPayment())
                .chequeraCuota(chequeraCuotaMapper.toDomain(entity.getChequeraCuota()))
                .reservaVacante(reservaVacanteMapper.toDomainModel(entity.getReservaVacante()))
                .build();
    }
}
