package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacionDecision;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraAdjudicacionEntity;

@Component
public class CompraAdjudicacionMapper {

    public CompraAdjudicacionEntity toEntity(CompraAdjudicacion domain) {
        if (domain == null) return null;
        return CompraAdjudicacionEntity.builder()
                .compraAdjudicacionId(domain.getCompraAdjudicacionId())
                .compraCotizacionId(domain.getCompraCotizacionId())
                .ejercicioId(domain.getEjercicioId())
                .compraCotizacionProveedorId(domain.getCompraCotizacionProveedorId())
                .proveedorAdjudicadoId(domain.getProveedorAdjudicadoId())
                .monto(domain.getMonto())
                .decision(domain.getDecision() != null ? domain.getDecision().name() : null)
                .fundamento(domain.getFundamento())
                .autoridadUsuarioId(domain.getAutoridadUsuarioId())
                .limiteAplicado(domain.getLimiteAplicado())
                .fecha(domain.getFecha())
                .build();
    }

    public CompraAdjudicacion toDomain(CompraAdjudicacionEntity entity) {
        if (entity == null) return null;
        return CompraAdjudicacion.builder()
                .compraAdjudicacionId(entity.getCompraAdjudicacionId())
                .compraCotizacionId(entity.getCompraCotizacionId())
                .ejercicioId(entity.getEjercicioId())
                .compraCotizacionProveedorId(entity.getCompraCotizacionProveedorId())
                .proveedorAdjudicadoId(entity.getProveedorAdjudicadoId())
                .monto(entity.getMonto())
                .decision(entity.getDecision() != null ? CompraAdjudicacionDecision.valueOf(entity.getDecision()) : null)
                .fundamento(entity.getFundamento())
                .autoridadUsuarioId(entity.getAutoridadUsuarioId())
                .limiteAplicado(entity.getLimiteAplicado())
                .fecha(entity.getFecha())
                .build();
    }
}
