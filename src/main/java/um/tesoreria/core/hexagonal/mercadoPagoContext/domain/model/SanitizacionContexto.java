package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model;

import java.util.List;

/**
 * Resultado de la sanitizacion de contextos activos duplicados de una cuota.
 */
public record SanitizacionContexto(Long chequeraCuotaId,
                                   Long conservadoId,
                                   List<Long> desactivados) {
}
