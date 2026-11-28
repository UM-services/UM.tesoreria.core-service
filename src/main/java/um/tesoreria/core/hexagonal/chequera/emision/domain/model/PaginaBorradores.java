package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import java.util.List;

/**
 * Página de borradores para el listado de la pantalla de emisión.
 *
 * <p>Se define en el dominio para no exponer {@code org.springframework.data.domain.Page} fuera de
 * la capa de persistencia.</p>
 */
public record PaginaBorradores(List<BorradorChequera> items, long total) {
}
