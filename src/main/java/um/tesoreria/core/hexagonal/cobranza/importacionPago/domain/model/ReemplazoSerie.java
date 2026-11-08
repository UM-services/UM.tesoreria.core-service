package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Vista mínima de {@code chequera_serie_reemplazo} necesaria para imputar un pago de una
 * chequera reemplazada.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReemplazoSerie {

    private Long chequeraReemplazoId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer alternativaId;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer lectivoId;
    private Integer geograficaId;

}
