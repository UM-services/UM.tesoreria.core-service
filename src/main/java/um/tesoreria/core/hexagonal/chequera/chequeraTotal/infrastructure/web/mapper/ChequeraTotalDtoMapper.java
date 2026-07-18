package um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.web.dto.ChequeraTotalResponse;

@Component
public class ChequeraTotalDtoMapper {

    public ChequeraTotalResponse toResponse(ChequeraTotal domain) {
        if (domain == null) return null;
        return ChequeraTotalResponse.builder()
                .chequeraTotalId(domain.getChequeraTotalId())
                .facultadId(domain.getFacultadId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .chequeraSerieId(domain.getChequeraSerieId())
                .productoId(domain.getProductoId())
                .total(domain.getTotal())
                .pagado(domain.getPagado())
                .build();
    }
}
