package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import java.util.List;

/**
 * Pedido de la etapa EMISION. Sin {@code borradorIds} se emiten todos los borradores del job.
 */
public record IncompletasEmisionRequest(List<Long> borradorIds) {
}
