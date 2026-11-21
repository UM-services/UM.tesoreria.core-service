package um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.entity.ValorMovimientoEntity;

@Component
public class ValorMovimientoMapper {

    public ValorMovimientoEntity toEntity(ValorMovimiento domain) {
        if (domain == null) {
            return null;
        }
        return ValorMovimientoEntity.builder()
                .valorMovimientoId(domain.valorMovimientoId())
                .valorId(domain.valorId())
                .fechaEmision(domain.fechaEmision())
                .numero(domain.numero())
                .bancariaIdOrigen(domain.bancariaIdOrigen())
                .fechaEfectivizacion(domain.fechaEfectivizacion())
                .importe(domain.importe())
                .estado(domain.estado())
                .nombreTitular(domain.nombreTitular())
                .cuitTitular(domain.cuitTitular())
                .reemplazo(domain.reemplazo())
                .fechaContable(domain.fechaContable())
                .ordenContable(domain.ordenContable())
                .letras(domain.letras())
                .build();
    }

    public ValorMovimiento toDomain(ValorMovimientoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ValorMovimiento(
                entity.getValorMovimientoId(),
                entity.getValorId(),
                entity.getFechaEmision(),
                entity.getNumero(),
                entity.getBancariaIdOrigen(),
                entity.getFechaEfectivizacion(),
                entity.getImporte(),
                entity.getEstado(),
                entity.getNombreTitular(),
                entity.getCuitTitular(),
                entity.getReemplazo(),
                entity.getFechaContable(),
                entity.getOrdenContable(),
                entity.getLetras());
    }
}
