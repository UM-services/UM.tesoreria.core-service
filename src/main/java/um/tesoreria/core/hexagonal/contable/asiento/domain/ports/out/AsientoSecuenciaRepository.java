package um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoSecuencia;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface AsientoSecuenciaRepository {

    /** Lee la fila de la fecha con bloqueo pesimista (serializa las reservas de esa fecha). */
    Optional<AsientoSecuencia> findWithLockByFecha(OffsetDateTime fecha);

    AsientoSecuencia save(AsientoSecuencia secuencia);

}
