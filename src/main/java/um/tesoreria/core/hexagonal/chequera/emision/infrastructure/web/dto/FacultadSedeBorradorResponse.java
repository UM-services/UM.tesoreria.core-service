package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

/**
 * Fila del resumen de borradores por facultad y sede (mismo shape que el resumen de chequeras).
 */
public record FacultadSedeBorradorResponse(Integer facultadId,
                                           String facultad,
                                           Integer geograficaId,
                                           String geografica,
                                           Long cantidad) {
}
