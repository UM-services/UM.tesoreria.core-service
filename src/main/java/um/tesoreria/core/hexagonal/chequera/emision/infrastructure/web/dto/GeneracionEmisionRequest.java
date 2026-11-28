package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import java.util.List;

/**
 * Pedido de la etapa EMISION desde un job de borradores. Sin {@code borradorIds} se emiten todos.
 */
public record GeneracionEmisionRequest(List<Long> borradorIds,
                                       Boolean forzarDeuda,
                                       String motivoOverride) {
}
