package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import java.util.List;

/**
 * Filtros del padrón de generación masiva de borradores y de la emisión directa.
 */
public record GeneracionFiltroRequest(Integer lectivoId,
                                      Integer facultadId,
                                      Integer geograficaId,
                                      Integer claseChequeraId,
                                      Integer tipoChequeraId,
                                      Boolean soloNoEnviadas,
                                      List<Long> chequeraSerieIds,
                                      Boolean forzarDeuda,
                                      String motivoOverride,
                                      List<Long> borradorIds) {
}
