package um.tesoreria.core.hexagonal.pagos.proveedorPago.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.infrastructure.persistence.entity.ProveedorPagoEntity;

@Component
public class ProveedorPagoMapper {

    public ProveedorPagoEntity toEntity(ProveedorPago domain) {
        if (domain == null) {
            return null;
        }
        return ProveedorPagoEntity.builder()
                .proveedorPagoId(domain.proveedorPagoId())
                .proveedorMovimientoIdPago(domain.proveedorMovimientoIdPago())
                .proveedorMovimientoIdFactura(domain.proveedorMovimientoIdFactura())
                .importeAplicado(domain.importeAplicado())
                .fechaPago(domain.fechaPago())
                .trazaContable(domain.trazaContable())
                .build();
    }

    public ProveedorPago toDomain(ProveedorPagoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ProveedorPago(
                entity.getProveedorPagoId(),
                entity.getProveedorMovimientoIdPago(),
                entity.getProveedorMovimientoIdFactura(),
                entity.getImporteAplicado(),
                entity.getFechaPago(),
                entity.getTrazaContable()
        );
    }
}
