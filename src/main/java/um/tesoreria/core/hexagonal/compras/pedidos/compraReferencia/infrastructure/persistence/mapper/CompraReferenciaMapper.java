package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.entity.CompraReferenciaEntity;

@Component
public class CompraReferenciaMapper {

    public CompraReferencia toDomain(CompraReferenciaEntity entity) {
        if (entity == null) return null;
        return CompraReferencia.builder()
                .ejercicioId(entity.getEjercicioId())
                .importe(entity.getImporte())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .build();
    }

    public CompraReferenciaEntity toEntity(CompraReferencia domain) {
        if (domain == null) return null;
        return CompraReferenciaEntity.builder()
                .ejercicioId(domain.getEjercicioId())
                .importe(domain.getImporte())
                .build();
    }

    /**
     * Aplica los campos del dominio sobre una entidad YA MANAGED. Los null se ignoran
     * ("no vino = no se toca").
     */
    public void updateEntity(CompraReferencia domain, CompraReferenciaEntity entity) {
        if (domain == null || entity == null) return;
        if (domain.getImporte() != null) entity.setImporte(domain.getImporte());
    }

}
