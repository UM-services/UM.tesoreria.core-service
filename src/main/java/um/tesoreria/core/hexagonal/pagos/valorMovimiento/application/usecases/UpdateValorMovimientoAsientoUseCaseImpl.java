package um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.exception.ValorMovimientoException;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in.UpdateValorMovimientoAsientoUseCase;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.out.ValorMovimientoRepository;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class UpdateValorMovimientoAsientoUseCaseImpl implements UpdateValorMovimientoAsientoUseCase {

    private final ValorMovimientoRepository valorMovimientoRepository;

    @Override
    public ValorMovimiento updateAsiento(Long valorMovimientoId, OffsetDateTime fechaContable, Integer ordenContable) {
        ValorMovimiento actual = valorMovimientoRepository.findById(valorMovimientoId)
                .orElseThrow(() -> new ValorMovimientoException(valorMovimientoId));
        ValorMovimiento actualizado = new ValorMovimiento(actual.valorMovimientoId(), actual.valorId(),
                actual.fechaEmision(), actual.numero(), actual.bancariaIdOrigen(), actual.fechaEfectivizacion(),
                actual.importe(), actual.estado(), actual.nombreTitular(), actual.cuitTitular(), actual.reemplazo(),
                fechaContable, ordenContable, actual.letras());
        return valorMovimientoRepository.save(actualizado);
    }
}
