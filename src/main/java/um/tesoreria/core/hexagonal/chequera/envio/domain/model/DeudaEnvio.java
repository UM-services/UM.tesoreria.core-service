package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Deuda de una persona, sólo con lo que necesita la regla de retención del legacy: por chequera,
 * su lectivo y la cantidad de cuotas impagas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeudaEnvio {

    private BigDecimal personaId;
    private Integer documentoId;
    private List<DeudaChequeraEnvio> deudas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeudaChequeraEnvio {
        private Integer lectivoId;
        private Integer cuotas;
    }
}
