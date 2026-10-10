package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.dto.CompraReferenciaRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.dto.CompraReferenciaResponse;

@Component
public class CompraReferenciaDtoMapper {

    public CompraReferencia toDomain(Integer ejercicioId, CompraReferenciaRequest request) {
        if (request == null) return null;
        return CompraReferencia.builder()
                .ejercicioId(ejercicioId)
                .importe(request.getImporte())
                .build();
    }

    public CompraReferenciaResponse toResponse(CompraReferencia domain) {
        if (domain == null) return null;
        return CompraReferenciaResponse.builder()
                .ejercicioId(domain.getEjercicioId())
                .importe(domain.getImporte())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .build();
    }

}
