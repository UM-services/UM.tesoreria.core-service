package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoRegistro;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface IngresoAsientoMapaRepository {

    Optional<AsientoIngresoRegistro> findByFechaAndTipoPago(OffsetDateTime fecha, Integer tipoPagoId);

    Optional<AsientoIngresoRegistro> findWithLockByFechaAndTipoPago(OffsetDateTime fecha, Integer tipoPagoId);

    AsientoIngresoRegistro create(OffsetDateTime fecha, Integer tipoPagoId, Integer ordenContable);

    AsientoIngresoRegistro updateOrden(AsientoIngresoRegistro registro, Integer ordenContable);

    /** Verdadero si otro tipo de pago de la misma fecha apunta al mismo orden. */
    boolean existsOtherByFechaAndOrden(OffsetDateTime fecha, Integer ordenContable, Integer tipoPagoId);

    void delete(AsientoIngresoRegistro registro);

}
