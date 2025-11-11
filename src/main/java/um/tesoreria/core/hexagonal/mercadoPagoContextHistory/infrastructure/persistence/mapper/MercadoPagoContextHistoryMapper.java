package um.tesoreria.core.hexagonal.mercadoPagoContextHistory.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.model.MercadoPagoContextHistory;
import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.infrastructure.persistence.entity.MercadoPagoContextHistoryEntity;
import um.tesoreria.core.model.MercadoPagoContext;

@Component
public class MercadoPagoContextHistoryMapper {

    public MercadoPagoContextHistoryEntity toEntityModel(MercadoPagoContextHistory mercadoPagoContextHistory) {
        if (mercadoPagoContextHistory == null) {
            return null;
        }
        return MercadoPagoContextHistoryEntity.builder()
                .mercadoPagoContextId(mercadoPagoContextHistory.getMercadoPagoContextId())
                .chequeraCuotaId(mercadoPagoContextHistory.getChequeraCuotaId())
                .initPoint(mercadoPagoContextHistory.getInitPoint())
                .fechaVencimiento(mercadoPagoContextHistory.getFechaVencimiento())
                .importe(mercadoPagoContextHistory.getImporte())
                .changed(mercadoPagoContextHistory.getChanged())
                .lastVencimientoUpdated(mercadoPagoContextHistory.getLastVencimientoUpdated())
                .preferenceId(mercadoPagoContextHistory.getPreferenceId())
                .preference(mercadoPagoContextHistory.getPreference())
                .activo(mercadoPagoContextHistory.getActivo())
                .chequeraPagoId(mercadoPagoContextHistory.getChequeraPagoId())
                .idMercadoPago(mercadoPagoContextHistory.getIdMercadoPago())
                .status(mercadoPagoContextHistory.getStatus())
                .fechaPago(mercadoPagoContextHistory.getFechaPago())
                .fechaAcreditacion(mercadoPagoContextHistory.getFechaAcreditacion())
                .importePagado(mercadoPagoContextHistory.getImportePagado())
                .payment(mercadoPagoContextHistory.getPayment())
                .build();
    }

    public MercadoPagoContextHistory toDomainModel(MercadoPagoContextHistoryEntity mercadoPagoContextHistoryEntity) {
        if (mercadoPagoContextHistoryEntity == null) {
            return null;
        }
        return MercadoPagoContextHistory.builder()
                .mercadoPagoContextHistoryId(mercadoPagoContextHistoryEntity.getMercadoPagoContextHistoryId())
                .mercadoPagoContextId(mercadoPagoContextHistoryEntity.getMercadoPagoContextId())
                .chequeraCuotaId(mercadoPagoContextHistoryEntity.getChequeraCuotaId())
                .initPoint(mercadoPagoContextHistoryEntity.getInitPoint())
                .fechaVencimiento(mercadoPagoContextHistoryEntity.getFechaVencimiento())
                .importe(mercadoPagoContextHistoryEntity.getImporte())
                .changed(mercadoPagoContextHistoryEntity.getChanged())
                .lastVencimientoUpdated(mercadoPagoContextHistoryEntity.getLastVencimientoUpdated())
                .preferenceId(mercadoPagoContextHistoryEntity.getPreferenceId())
                .preference(mercadoPagoContextHistoryEntity.getPreference())
                .activo(mercadoPagoContextHistoryEntity.getActivo())
                .chequeraPagoId(mercadoPagoContextHistoryEntity.getChequeraPagoId())
                .idMercadoPago(mercadoPagoContextHistoryEntity.getIdMercadoPago())
                .status(mercadoPagoContextHistoryEntity.getStatus())
                .fechaPago(mercadoPagoContextHistoryEntity.getFechaPago())
                .fechaAcreditacion(mercadoPagoContextHistoryEntity.getFechaAcreditacion())
                .importePagado(mercadoPagoContextHistoryEntity.getImportePagado())
                .payment(mercadoPagoContextHistoryEntity.getPayment())
                .build();
    }

    public MercadoPagoContextHistory fromContext(MercadoPagoContext mercadoPagoContext) {
        if (mercadoPagoContext == null) {
            return null;
        }
        return MercadoPagoContextHistory.builder()
                .mercadoPagoContextId(mercadoPagoContext.getMercadoPagoContextId())
                .chequeraCuotaId(mercadoPagoContext.getChequeraCuotaId())
                .initPoint(mercadoPagoContext.getInitPoint())
                .fechaVencimiento(mercadoPagoContext.getFechaVencimiento())
                .importe(mercadoPagoContext.getImporte())
                .changed(mercadoPagoContext.getChanged())
                .lastVencimientoUpdated(mercadoPagoContext.getLastVencimientoUpdated())
                .preferenceId(mercadoPagoContext.getPreferenceId())
                .preference(mercadoPagoContext.getPreference())
                .activo(mercadoPagoContext.getActivo())
                .chequeraPagoId(mercadoPagoContext.getChequeraPagoId())
                .idMercadoPago(mercadoPagoContext.getIdMercadoPago())
                .status(mercadoPagoContext.getStatus())
                .fechaPago(mercadoPagoContext.getFechaPago())
                .fechaAcreditacion(mercadoPagoContext.getFechaAcreditacion())
                .importePagado(mercadoPagoContext.getImportePagado())
                .payment(mercadoPagoContext.getPayment())
                .build();

    }

}
