package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import java.time.OffsetDateTime;

public interface DeleteAsientoUseCase {
    void deleteAsiento(OffsetDateTime fechaContable, Integer ordenContable);
}
