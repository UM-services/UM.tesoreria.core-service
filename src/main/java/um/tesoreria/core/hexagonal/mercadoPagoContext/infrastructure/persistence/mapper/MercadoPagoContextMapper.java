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
        return MercadoPagoContextEntity.builder()
                .mercadoPagoContextId(domain.getMercadoPagoContextId())
                .chequeraCuotaId(domain.getChequeraCuotaId())
                .reservaVacanteId(domain.getReservaVacanteId())
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
