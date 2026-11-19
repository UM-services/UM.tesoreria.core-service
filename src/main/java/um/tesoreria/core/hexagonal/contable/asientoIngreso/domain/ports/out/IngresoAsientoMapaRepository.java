package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoRegistro;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface IngresoAsientoMapaRepository {

    Optional<AsientoIngresoRegistro> findByFechaAndTipoPago(OffsetDateTime fecha, Integer tipoPagoId);

    Optional<AsientoIngresoRegistro> findWithLockByFechaAndTipoPago(OffsetDateTime fecha, Integer tipoPagoId);

    AsientoIngresoRegistro create(OffsetDateTime fecha, Integer tipoPagoId, Integer ordenContable);

    void delete(AsientoIngresoRegistro registro);

}
