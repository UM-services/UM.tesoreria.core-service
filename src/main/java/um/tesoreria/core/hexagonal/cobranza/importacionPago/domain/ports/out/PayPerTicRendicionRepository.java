package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out;

import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.RendicionPayPerTic;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Acceso a las filas de {@code paypertic} que todavía no fueron imputadas.
 */
public interface PayPerTicRendicionRepository {

    List<RendicionPayPerTic> findPendientesByPeriodo(OffsetDateTime desde, OffsetDateTime hasta);

    void marcarImportado(String payperticId);

}
