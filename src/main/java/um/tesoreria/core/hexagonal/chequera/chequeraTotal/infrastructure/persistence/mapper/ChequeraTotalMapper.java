package um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.persistence.entity.ChequeraTotalEntity;

@Component
public class ChequeraTotalMapper {

    public ChequeraTotal toDomain(ChequeraTotalEntity entity) {
        if (entity == null) return null;
        return ChequeraTotal.builder()
                .chequeraTotalId(entity.getChequeraTotalId())
                .facultadId(entity.getFacultadId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .chequeraSerieId(entity.getChequeraSerieId())
                .productoId(entity.getProductoId())
                .total(entity.getTotal())
                .pagado(entity.getPagado())
                .build();
    }

    public ChequeraTotalEntity toEntity(ChequeraTotal domain) {
        if (domain == null) return null;
        ChequeraTotalEntity.ChequeraTotalEntityBuilder builder = ChequeraTotalEntity.builder()
                .chequeraTotalId(domain.getChequeraTotalId())
                .facultadId(domain.getFacultadId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .chequeraSerieId(domain.getChequeraSerieId())
                .productoId(domain.getProductoId());

        if (domain.getTotal() != null) builder.total(domain.getTotal());
        if (domain.getPagado() != null) builder.pagado(domain.getPagado());

        return builder.build();
    }
}
