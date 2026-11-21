package um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;

import java.time.OffsetDateTime;

/**
 * Asigna (o corrige) el asiento contable de un valor ya emitido. Se usa al reconstruir la traza
 * de una orden de pago cuyos valores todavía no tenían asiento.
 */
public interface UpdateValorMovimientoAsientoUseCase {

    ValorMovimiento updateAsiento(Long valorMovimientoId, OffsetDateTime fechaContable, Integer ordenContable);
}
