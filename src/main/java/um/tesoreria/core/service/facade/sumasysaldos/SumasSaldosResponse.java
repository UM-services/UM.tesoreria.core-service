package um.tesoreria.core.service.facade.sumasysaldos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Balance de sumas y saldos en JSON: una fila por cuenta imputable (grado 5) y los totales por columna. */
public record SumasSaldosResponse(OffsetDateTime desde, OffsetDateTime hasta, List<SumasSaldosFila> cuentas,
                                  Totales totales) {

    public record Totales(BigDecimal inicialDebitos, BigDecimal inicialCreditos, BigDecimal debitos,
                          BigDecimal creditos, BigDecimal saldoDeudor, BigDecimal saldoAcreedor) {
    }
}