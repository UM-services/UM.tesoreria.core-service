package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

/**
 * Filtro del listado liviano de borradores abiertos (pantalla «Borradores abiertos»).
 *
 * <p>Sin {@code @RequestParam}: el filtro compuesto viaja en el body.</p>
 */
public record BorradorListadoRequest(Integer lectivoId,
                                     Integer facultadId,
                                     Integer geograficaId,
                                     Integer page,
                                     Integer size) {
}
