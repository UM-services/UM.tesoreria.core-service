package um.tesoreria.core.hexagonal.contable.bancoMovimiento.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model.BancoMovimiento;
import um.tesoreria.core.hexagonal.contable.bancoMovimiento.infrastructure.persistence.entity.BancoMovimientoEntity;

@Component
public class BancoMovimientoMapper {

    public BancoMovimientoEntity toEntity(BancoMovimiento domain) {
        if (domain == null) {
            return null;
        }
        return BancoMovimientoEntity.builder()
                .bancoMovimientoId(domain.bancoMovimientoId())
                .bancariaId(domain.bancariaId())
                .fecha(domain.fecha())
                .orden(domain.orden())
                .tipo(domain.tipo())
                .numeroComprobante(domain.numeroComprobante())
                .concepto(domain.concepto())
                .importe(domain.importe())
                .numeroCuenta(domain.numeroCuenta())
                .debita(domain.debita())
                .fechaConciliacion(domain.fechaConciliacion())
                .anulado(domain.anulado())
                .fechaContable(domain.fechaContable())
                .ordenContable(domain.ordenContable())
                .valorMovimientoId(domain.valorMovimientoId())
                .build();
    }

    public BancoMovimiento toDomain(BancoMovimientoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new BancoMovimiento(
                entity.getBancoMovimientoId(),
                entity.getBancariaId(),
                entity.getFecha(),
                entity.getOrden(),
                entity.getTipo(),
                entity.getNumeroComprobante(),
                entity.getConcepto(),
                entity.getImporte(),
                entity.getNumeroCuenta(),
                entity.getDebita(),
                entity.getFechaConciliacion(),
                entity.getAnulado(),
                entity.getFechaContable(),
                entity.getOrdenContable(),
                entity.getValorMovimientoId()
        );
    }
}
