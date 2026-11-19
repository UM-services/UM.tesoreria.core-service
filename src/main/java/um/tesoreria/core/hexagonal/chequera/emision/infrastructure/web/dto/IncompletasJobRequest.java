package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import java.util.List;

/**
 * Pedido de la etapa BORRADORES. Sin {@code chequeraIds} el backend arma el padrón con la misma
 * consulta que la pantalla (vista {@code vw_chequera_incompleta}).
 */
public record IncompletasJobRequest(
        Integer lectivoId,
        Integer facultadId,
        Integer geograficaId,
        Integer claseChequeraId,
        Boolean soloNoEnviadas,
        List<Long> chequeraIds) {
}
