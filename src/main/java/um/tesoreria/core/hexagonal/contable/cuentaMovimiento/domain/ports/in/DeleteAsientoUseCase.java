package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import java.time.OffsetDateTime;

public interface DeleteAsientoUseCase {
    void deleteAsiento(OffsetDateTime fechaContable, Integer ordenContable);

    /**
     * Borra sólo las líneas del comprobante indicado. Si el asiento tiene líneas de otro
     * comprobante, aborta con {@code AsientoConflictoException}.
     */
    void deleteAsiento(OffsetDateTime fechaContable, Integer ordenContable, Integer comprobanteId);
}
