package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Cambios parciales sobre los datos de preferencia de un contexto.
 *
 * <p>Todos los campos son nullable: {@code null} significa "no modificar". Se usa un record
 * (y no la entidad de dominio) justamente para no arrastrar los valores por defecto
 * {@code importe = 0} / {@code changed = 0}.</p>
 */
public record PreferenciaPatch(OffsetDateTime fechaVencimiento,
                               BigDecimal importe,
                               String initPoint,
                               String preferenceId,
                               String preference,
                               Byte changed,
                               OffsetDateTime lastVencimientoUpdated) {
}
