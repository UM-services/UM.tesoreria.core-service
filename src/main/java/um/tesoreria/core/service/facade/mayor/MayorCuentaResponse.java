package um.tesoreria.core.service.facade.mayor;

import java.math.BigDecimal;
import java.util.List;

public record MayorCuentaResponse(
        BigDecimal numeroCuenta,
        String nombre,
        MayorSaldoInicialResponse saldoInicial,
        List<MayorMovimientoResponse> movimientos,
        BigDecimal totalDebe,
        BigDecimal totalHaber,
        BigDecimal saldoFinal) {
}