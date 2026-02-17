package um.tesoreria.core.hexagonal.chequeraCuota.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraTotal;

@Component
public class ChequeraCuotaMapper {

    public ChequeraCuota toDomain(um.tesoreria.core.kotlin.model.ChequeraCuota entity) {
        if (entity == null) {
            return null;
        }
        return ChequeraCuota.builder()
                .chequeraCuotaId(entity.getChequeraCuotaId())
                .chequeraId(entity.getChequeraId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .productoId(entity.getProductoId())
                .alternativaId(entity.getAlternativaId())
                .cuotaId(entity.getCuotaId())
                .mes(entity.getMes())
                .anho(entity.getAnho())
                .importe1(entity.getImporte1())
                .vencimiento1(entity.getVencimiento1())
                .pagado(entity.getPagado())
                .baja(entity.getBaja())
                .compensada(entity.getCompensada())
                .build();
    }

    public ChequeraPago toDomain(um.tesoreria.core.kotlin.model.ChequeraPago entity) {
        if (entity == null) {
            return null;
        }
        return ChequeraPago.builder()
                .chequeraPagoId(entity.getChequeraPagoId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .productoId(entity.getProductoId())
                .alternativaId(entity.getAlternativaId())
                .cuotaId(entity.getCuotaId())
                .importe(entity.getImporte())
                .build();
    }

    public ChequeraTotal toDomain(um.tesoreria.core.model.ChequeraTotal entity) {
        if (entity == null) {
            return null;
        }
        return ChequeraTotal.builder()
                .chequeraTotalId(entity.getChequeraTotalId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .productoId(entity.getProductoId())
                .total(entity.getTotal())
                .build();
    }

}
